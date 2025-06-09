package com.aiolos.live.common.keys.builder;

import com.aiolos.live.common.keys.RedisKeyProperties;

public class MsgProviderRedisKeyBuilder extends RedisKeyBuilder {

    private static final String PREVENT_REPEAT_SENDING_KEY = "sms:prevent";
    
    public MsgProviderRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }
    
    public String buildPreventRepeatSendingKey(String phone) {
        return super.getPrefix() + PREVENT_REPEAT_SENDING_KEY + super.getSplit() + phone;
    }
}
