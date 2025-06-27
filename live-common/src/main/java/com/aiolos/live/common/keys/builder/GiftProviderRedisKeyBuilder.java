package com.aiolos.live.common.keys.builder;

import com.aiolos.live.common.keys.RedisKeyProperties;

public class GiftProviderRedisKeyBuilder extends RedisKeyBuilder {

    private static final String GIFT_CONFIG_KEY = "gift:config";
    private static final String GIFT_CONFIG_LIST_KEY = "gift:config:list";
    
    public GiftProviderRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }
    
    public String buildGiftConfigKey(Long id) {
        return super.getPrefix() + GIFT_CONFIG_KEY + getSplit() + id;
    }
    
    public String buildGiftConfigListKey() {
        return super.getPrefix() + GIFT_CONFIG_LIST_KEY;
    }
}
