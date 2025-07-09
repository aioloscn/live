package com.aiolos.live.im.provider.service.impl;

import com.aiolos.live.common.keys.builder.ImProviderRedisKeyBuilder;
import com.aiolos.live.im.provider.service.ImTokenService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ImTokenServiceImpl implements ImTokenService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImProviderRedisKeyBuilder imProviderRedisKeyBuilder;
    
    @Override
    public String createImLoginToken(Long userId, Integer appId) {
        String token = UUID.randomUUID().toString().replace("-", "") + "%23" + appId; // 前端创建WebSocket会用到，所以不能用#，片段标识符后面内容不会发送到服务器
        redisTemplate.opsForValue().set(imProviderRedisKeyBuilder.buildImLoginTokenKey(token), userId, 5, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        Object obj = redisTemplate.opsForValue().get(imProviderRedisKeyBuilder.buildImLoginTokenKey(token));
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return (Long) obj;
        } else {
            return null;
        }
    }
}
