package com.aiolos.live.user.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtils;
import com.aiolos.live.common.keys.UserProviderRedisKeyBuilder;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.mq.producer.UpdateUserInfoProducer;
import com.aiolos.live.user.provider.po.User;
import com.aiolos.live.user.provider.mapper.UserMapper;
import com.aiolos.live.user.provider.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Aiolos
 * @since 2025-03-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserMapper userMapper;
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
            return ConvertBeanUtils.convert(obj, UserDTO.class);
        }
        UserDTO userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
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
        userMapper.insert(ConvertBeanUtils.convert(userDTO, User.class));
    }

    @Override
    public void updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return;
        }
        int updated = userMapper.updateById(ConvertBeanUtils.convert(userDTO, User.class));
        if (updated > 0) {
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
                .filter(Objects::nonNull).map(x -> ConvertBeanUtils.convert(x, UserDTO.class)).collect(Collectors.toList());

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
                                    .flatMap(ids -> ConvertBeanUtils.convertList(userMapper.selectBatchIds(ids), UserDTO.class).stream())
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
