package com.aiolos.live.im.provider.service.impl;

import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.im.provider.service.ImOnlineService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImOnlineServiceImpl implements ImOnlineService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder;
    
    @Override
    public boolean isOnline(Integer appId, Long userId) {
        return stringRedisTemplate.hasKey(imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(appId, userId));
    }
}
