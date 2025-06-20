package com.aiolos.live.common.keys.builder.common;

import com.aiolos.live.common.keys.RedisKeyProperties;
import com.aiolos.live.common.keys.builder.RedisKeyBuilder;

public class ImCoreServerCommonRedisKeyBuilder extends RedisKeyBuilder {

    private static final String IM_BIND_IP_KEY = "im:bind:ip";
    
    public ImCoreServerCommonRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }

    public String buildImBindIpKey(Integer appId, Long userId) {
        return IM_BIND_IP_KEY + getSplit() + appId + "%23" + userId;
    }
}
