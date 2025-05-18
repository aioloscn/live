package com.aiolos.live.common.keys.builder;

import com.aiolos.live.common.keys.RedisKeyProperties;

public class ImProviderRedisKeyBuilder extends RedisKeyBuilder {

    private static final String IM_LOGIN_TOKEN_KEY = "im:loginToken";
    
    public ImProviderRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }
    
    public String buildImLoginTokenKey(String token) {
        return super.getPrefix() + IM_LOGIN_TOKEN_KEY + getSplit() + token;
    }
}
