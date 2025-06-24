package com.aiolos.live.account.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.account.dto.AccountDTO;
import com.aiolos.live.account.provider.service.AccountTokenService;
import com.aiolos.live.account.provider.utils.AnonymousIdGenerator;
import com.aiolos.live.common.keys.builder.common.UserProviderCommonRedisKeyBuilder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AccountTokenServiceImpl implements AccountTokenService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserProviderCommonRedisKeyBuilder userProviderCommonRedisKeyBuilder;
    @Resource
    private AnonymousIdGenerator anonymousIdGenerator;

    @Override
    public String createToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(userProviderCommonRedisKeyBuilder.buildUserTokenKey(token), userId, 7, TimeUnit.DAYS);
        return token;
    }

    @Override
    public AccountDTO getUserByToken(String token) {
        AccountDTO dto = new AccountDTO();
        Long userId = null;
        Object obj = redisTemplate.opsForValue().get(userProviderCommonRedisKeyBuilder.buildUserTokenKey(token));
        if (obj instanceof Integer) {
            userId = ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            userId = (Long) obj;
        } else {
            return dto;
        }
        dto.setUserId(userId);
        Object userObj = redisTemplate.opsForValue().get(userProviderCommonRedisKeyBuilder.buildUserInfoKey(userId));
        if (userObj != null) {
            dto = ConvertBeanUtil.convert(userObj, AccountDTO.class);
        }

        return dto;
    }

    @Override
    public Long getOrCreateAnonymousId(String deviceId) {
        String key = userProviderCommonRedisKeyBuilder.buildAnonymousIdKey(deviceId);
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj != null) {
            return (Long) obj;
        }

        long anonymousId = anonymousIdGenerator.generateAnonymousId();
        redisTemplate.opsForValue().set(key, anonymousId, 7, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(userProviderCommonRedisKeyBuilder.buildAnonymousDeviceIdKey(anonymousId), deviceId, 7, TimeUnit.DAYS);
        
        return anonymousId;
    }
}
