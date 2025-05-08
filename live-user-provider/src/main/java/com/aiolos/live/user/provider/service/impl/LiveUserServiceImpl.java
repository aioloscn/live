package com.aiolos.live.user.provider.service.impl;

import com.aiolos.common.enums.errors.ErrorEnum;
import com.aiolos.common.exception.utils.ExceptionUtil;
import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.common.keys.builder.MsgProviderRedisBuilder;
import com.aiolos.live.common.keys.builder.UserProviderRedisKeyBuilder;
import com.aiolos.live.id.generator.enums.IdPolicyEnum;
import com.aiolos.live.id.generator.interfaces.IdGeneratorRpc;
import com.aiolos.live.model.po.User;
import com.aiolos.live.service.UserService;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.config.UserThreadPoolManager;
import com.aiolos.live.user.provider.model.bo.LoginBO;
import com.aiolos.live.user.provider.model.vo.UserVO;
import com.aiolos.live.user.provider.mq.producer.UpdateUserInfoProducer;
import com.aiolos.live.user.provider.service.LiveUserService;
import com.alibaba.nacos.common.utils.ConvertUtils;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    
    @Override
    public String createToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(userProviderRedisKeyBuilder.buildUserTokenKey(token), userId, userProviderRedisKeyBuilder.get7DaysExpiration(), TimeUnit.SECONDS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        Object obj = redisTemplate.opsForValue().get(userProviderRedisKeyBuilder.buildUserTokenKey(token));
        if (obj != null) {
            return ConvertUtils.toLong(obj);
        }
        return null;
    }

    @Override
    public UserVO login(LoginBO loginBO, HttpServletResponse response) {
        
        if (StringUtils.isBlank(loginBO.getCode()) || StringUtils.isBlank(loginBO.getCode())) {
            ExceptionUtil.throwException(ErrorEnum.BIND_EXCEPTION_ERROR);
        }

        String smsRedisKey = msgProviderRedisBuilder.buildSmsLoginCodeKey(loginBO.getPhone());
        Object redisVal = redisTemplate.opsForValue().get(smsRedisKey);
        if (redisVal == null) {
            ExceptionUtil.throwException(ErrorEnum.SMS_CODE_EXPIRED);
        }

        String cacheCode = redisVal.toString();
        if (!cacheCode.equals(loginBO.getCode())) {
            ExceptionUtil.throwException(ErrorEnum.SMS_CODE_INCORRECT);
        }

        // 未注册则注册
        UserVO userVO = this.queryByPhone(loginBO.getPhone());
        if (userVO == null) {
            User newUser = new User();
            // 即便获取不到分布式id，mybatis-plus的@TableId("user_id")会隐式创建一个Long类型id，type=IdType.NONE也一样
            // 如果ShardingSphere有配置keyGenerateStrategy也会自动生成主键，可以设置雪花算法
            newUser.setUserId(idGeneratorRpc.getNonSeqId(IdPolicyEnum.USER_ID_POLICY.getPrimaryKey()));
            newUser.setNickName("用户_" + RandomUtils.secure().randomInt(10000000, 99999999));
            newUser.setPhone(loginBO.getPhone());
            userVO = ConvertBeanUtil.convert(newUser, UserVO::new);
            // 得保证主线程不会有异常
            UserThreadPoolManager.commonAsyncPool.execute(() -> userService.save(newUser));
        }

        redisTemplate.opsForValue().set(userProviderRedisKeyBuilder.buildUserInfoKey(userVO.getUserId()), userVO, userProviderRedisKeyBuilder.get7DaysExpiration(), TimeUnit.SECONDS);

        redisTemplate.delete(smsRedisKey);
        redisTemplate.delete(userProviderRedisKeyBuilder.buildUserPhoneKey(loginBO.getPhone()));

        String token = this.createToken(userVO.getUserId());
        Cookie cookie = new Cookie("live-token", token);
        cookie.setDomain("live.aiolos.com");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.addCookie(cookie);
        return userVO;
    }

    @Override
    public UserVO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        
        String userKey = userProviderRedisKeyBuilder.buildUserInfoKey(userId);
        Object obj = redisTemplate.opsForValue().get(userKey);
        if (obj != null) {
            UserVO userVO = ConvertBeanUtil.convert(obj, UserVO::new);
            return userVO.getUserId() != null ? userVO : null;
        }
        UserVO userVO = ConvertBeanUtil.convert(userService.getById(userId), UserVO::new);
        if (userVO != null) {
            redisTemplate.opsForValue().set(userKey, userVO, userProviderRedisKeyBuilder.get7DaysExpiration(), TimeUnit.SECONDS);
        } else {
            // 缓存空值
            redisTemplate.opsForValue().set(userKey, new UserVO(), 60, TimeUnit.SECONDS);
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

    @Override
    public UserVO queryByPhone(String phone) {
        if (StringUtils.isBlank(phone))
            return null;
        String key = userProviderRedisKeyBuilder.buildUserPhoneKey(phone);
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj != null) {
            // 可能是空缓存
            UserVO userVO = ConvertBeanUtil.convert(obj, UserVO::new);
            if (userVO.getUserId() == null) {
                return null;
            }
            return userVO;
        }
        
        User user = userService.lambdaQuery().eq(User::getPhone, phone).one();
        if (user != null) {
            UserVO userVO = ConvertBeanUtil.convert(user, UserVO::new);
            redisTemplate.opsForValue().set(key, userVO, 30, TimeUnit.MINUTES);
            return userVO;
        }
        
        // 防止缓存穿透，设置空缓存
        redisTemplate.opsForValue().set(key, new UserVO(), 60, TimeUnit.SECONDS);
        return null;
    }
}
