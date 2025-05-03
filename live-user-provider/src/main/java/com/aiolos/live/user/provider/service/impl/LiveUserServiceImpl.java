package com.aiolos.live.user.provider.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.common.enums.errors.ErrorEnum;
import com.aiolos.common.exception.utils.ExceptionUtil;
import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.common.keys.MsgProviderRedisBuilder;
import com.aiolos.live.common.keys.UserProviderRedisKeyBuilder;
import com.aiolos.live.id.generator.enums.IdPolicyEnum;
import com.aiolos.live.id.generator.interfaces.IdGeneratorRpc;
import com.aiolos.live.model.po.User;
import com.aiolos.live.service.UserService;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.config.UserThreadPoolManager;
import com.aiolos.live.user.provider.model.bo.LoginBO;
import com.aiolos.live.user.provider.model.vo.UserVO;
import com.aiolos.live.user.provider.mq.producer.UpdateUserInfoProducer;
import com.aiolos.live.user.provider.nacos.CustomizeConfigurationProperties;
import com.aiolos.live.user.provider.service.LiveUserService;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LiveUserServiceImpl implements LiveUserService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Resource
    private UserProviderRedisKeyBuilder userProviderRedisKeyBuilder;
    @Resource
    private MsgProviderRedisBuilder msgProviderRedisBuilder;
    @Autowired
    private UpdateUserInfoProducer updateUserInfoProducer;
    @DubboReference
    private IdGeneratorRpc idGeneratorRpc;
    
    @Autowired
    private CustomizeConfigurationProperties customizeConfigurationProperties;

    @Override
    public UserVO login(LoginBO loginBO) {
        
        if (StringUtils.isBlank(loginBO.getCode()) || StringUtils.isBlank(loginBO.getCode())) {
            ExceptionUtil.throwException(ErrorEnum.BIND_EXCEPTION_ERROR);
        }

        String smsRedisKey = msgProviderRedisBuilder.buildSmsLoginCodeKey(loginBO.getPhone());
        String cacheCode = redisTemplate.opsForValue().get(smsRedisKey).toString();
        if (StringUtils.isBlank(cacheCode)) {
            ExceptionUtil.throwException(ErrorEnum.SMS_CODE_EXPIRED);
        }
        if (!cacheCode.equals(loginBO.getCode())) {
            ExceptionUtil.throwException(ErrorEnum.SMS_CODE_INCORRECT);
        }

        // 未注册则注册
        UserVO userVO;
        List<User> userList = userService.lambdaQuery().eq(User::getPhone, loginBO.getPhone()).list();
        if (CollectionUtil.isEmpty(userList)) {
            User newUser = new User();
            newUser.setUserId(idGeneratorRpc.getSeqId(IdPolicyEnum.USER_ID_POLICY.getPrimaryKey()));
            newUser.setNickName("用户_" + RandomUtils.secure().randomInt(10000000, 99999999));
            newUser.setPhone(loginBO.getPhone());
            userVO = ConvertBeanUtil.convert(newUser, UserVO::new);
            // 得保证主线程不会有异常
            UserThreadPoolManager.commonAsyncPool.execute(() -> userService.save(newUser));
        } else {
            userVO = ConvertBeanUtil.convert(userList.get(0), UserVO::new);
        }

        String token = UUID.randomUUID().toString();
        userVO.setToken(token);
        redisTemplate.opsForValue().set(userProviderRedisKeyBuilder.buildUserTokenKey(token), userVO.getUserId(), userProviderRedisKeyBuilder.get7DaysExpiration(), TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(userProviderRedisKeyBuilder.buildUserInfoKey(userVO.getUserId()), userVO, userProviderRedisKeyBuilder.get7DaysExpiration(), TimeUnit.SECONDS);

        redisTemplate.delete(smsRedisKey);
        return userVO;
    }

    @Override
    public UserVO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        
        log.info("自动刷新配置中的值：{}", customizeConfigurationProperties.getTestRefresh());
        
        Object obj = redisTemplate.opsForValue().get(userProviderRedisKeyBuilder.buildUserInfoKey(userId));
        if (obj != null) {
            return ConvertBeanUtil.convert(obj, UserVO::new);
        }
        UserVO userVO = ConvertBeanUtil.convert(userService.getById(userId), UserVO::new);
        if (userVO != null) {
            redisTemplate.opsForValue().set(userProviderRedisKeyBuilder.buildUserInfoKey(userId), userVO, userProviderRedisKeyBuilder.get7DaysExpiration(), TimeUnit.SECONDS);
        }
        return userVO;
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
        // 目前情况分库分表
        boolean updated = userService.updateById(ConvertBeanUtil.convert(userDTO, User::new));
        if (updated) {
            redisTemplate.delete(userProviderRedisKeyBuilder.buildUserInfoKey(userDTO.getUserId()));
            updateUserInfoProducer.deleteUserCache(userDTO.getUserId());
        }
    }

    @Override
    public Map<Long, UserVO> batchQueryUserInfo(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }
        userIds = userIds.stream().filter(id -> id >= 100).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }

        List<String> keyList = userIds.stream().map(userId -> userProviderRedisKeyBuilder.buildUserInfoKey(userId)).collect(Collectors.toList());
        List<UserVO> dtoListInRedis = redisTemplate.opsForValue().multiGet(keyList).stream()
                .filter(Objects::nonNull).map(x -> ConvertBeanUtil.convert(x, UserVO::new)).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(dtoListInRedis) && dtoListInRedis.size() == userIds.size()) {
            return dtoListInRedis.stream().collect(Collectors.toMap(UserVO::getUserId, Function.identity()));
        }

        List<Long> keyInRedis = dtoListInRedis.stream().map(UserVO::getUserId).collect(Collectors.toList());
        List<Long> keyNotInRedis = userIds.stream().filter(id -> !keyInRedis.contains(id)).collect(Collectors.toList());

        Map<Long, List<Long>> userIdMap = keyNotInRedis.stream().collect(Collectors.groupingBy(id -> id % 100));

        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        List<UserVO> dbQueryResult = new ArrayList<>();
        try {
            dbQueryResult = forkJoinPool.submit(() ->
                    userIdMap.values().parallelStream()
                            .flatMap(ids -> ConvertBeanUtil.convertList(userService.listByIds(ids), UserVO::new).stream())
                            .collect(Collectors.toList())
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            forkJoinPool.shutdown();
        }

        // 从数据库查询出来的数据缓存到redis
        if (!CollectionUtils.isEmpty(dbQueryResult)) {
            Map<String, UserVO> dbQueryMap = dbQueryResult.stream().collect(Collectors.toMap(dto -> userProviderRedisKeyBuilder.buildUserInfoKey(dto.getUserId()), Function.identity()));
            redisTemplate.opsForValue().multiSet(dbQueryMap);
            // 批量设置过期时间
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                dbQueryMap.keySet().forEach(key -> connection.keyCommands().expire(key.getBytes(), userProviderRedisKeyBuilder.get7DaysExpiration() + userProviderRedisKeyBuilder.randomExpireSeconds()));
                return null;
            });

            dbQueryResult.addAll(dtoListInRedis);
        }

        return dbQueryResult.stream().collect(Collectors.toMap(UserVO::getUserId, Function.identity()));
    }
}
