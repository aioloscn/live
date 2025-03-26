package com.aiolos.live.user.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.common.keys.UserProviderRedisKeyBuilder;
import com.aiolos.live.model.po.User;
import com.aiolos.live.service.UserService;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.mq.producer.UpdateUserInfoProducer;
import com.aiolos.live.user.provider.service.LiveUserService;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LiveUserServiceImpl implements LiveUserService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Resource
    private UserProviderRedisKeyBuilder userProviderRedisKeyBuilder;
    @Autowired
    private UpdateUserInfoProducer updateUserInfoProducer;

    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(userProviderRedisKeyBuilder.buildUserInfoKey(userId));
        if (obj != null) {
            return ConvertBeanUtil.convert(obj, UserDTO::new);
        }
        UserDTO userDTO = ConvertBeanUtil.convert(userService.getById(userId), UserDTO::new);
        if (userDTO != null) {
            redisTemplate.opsForValue().set(userProviderRedisKeyBuilder.buildUserInfoKey(userId), userDTO, userProviderRedisKeyBuilder.getExpire(), TimeUnit.SECONDS);
        }
        return userDTO;
    }

    @Override
    public void insertUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return;
        }
        userService.save(ConvertBeanUtil.convert(userDTO, User::new));
    }

    @Override
    public void updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return;
        }
        boolean updated = userService.updateById(ConvertBeanUtil.convert(userDTO, User::new));
        if (updated) {
            redisTemplate.delete(userProviderRedisKeyBuilder.buildUserInfoKey(userDTO.getUserId()));
            updateUserInfoProducer.deleteUserCache(userDTO);
        }
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }
        userIds = userIds.stream().filter(id -> id >= 100).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }

        List<String> keyList = userIds.stream().map(userId -> userProviderRedisKeyBuilder.buildUserInfoKey(userId)).collect(Collectors.toList());
        List<UserDTO> dtoListInRedis = redisTemplate.opsForValue().multiGet(keyList).stream()
                .filter(Objects::nonNull).map(x -> ConvertBeanUtil.convert(x, UserDTO::new)).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(dtoListInRedis) && dtoListInRedis.size() == userIds.size()) {
            return dtoListInRedis.stream().collect(Collectors.toMap(UserDTO::getUserId, Function.identity()));
        }

        List<Long> keyInRedis = dtoListInRedis.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Long> keyNotInRedis = userIds.stream().filter(id -> !keyInRedis.contains(id)).collect(Collectors.toList());

        Map<Long, List<Long>> userIdMap = keyNotInRedis.stream().collect(Collectors.groupingBy(id -> id % 100));

        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        List<UserDTO> dbQueryResult = new ArrayList<>();
        try {
            dbQueryResult = forkJoinPool.submit(() ->
                    userIdMap.values().parallelStream()
                            .flatMap(ids -> ConvertBeanUtil.convertList(userService.listByIds(ids), UserDTO::new).stream())
                            .collect(Collectors.toList())
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            forkJoinPool.shutdown();
        }

        // 从数据库查询出来的数据缓存到redis
        if (!CollectionUtils.isEmpty(dbQueryResult)) {
            Map<String, UserDTO> dbQueryMap = dbQueryResult.stream().collect(Collectors.toMap(dto -> userProviderRedisKeyBuilder.buildUserInfoKey(dto.getUserId()), Function.identity()));
            redisTemplate.opsForValue().multiSet(dbQueryMap);
            // 批量设置过期时间
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                dbQueryMap.keySet().forEach(key -> connection.keyCommands().expire(key.getBytes(), userProviderRedisKeyBuilder.getExpire() + userProviderRedisKeyBuilder.randomExpireSeconds()));
                return null;
            });

            dbQueryResult.addAll(dtoListInRedis);
        }

        return dbQueryResult.stream().collect(Collectors.toMap(UserDTO::getUserId, Function.identity()));
    }
}
