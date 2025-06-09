package com.aiolos.live.common.keys.builder.common;

import com.aiolos.live.common.keys.RedisKeyProperties;
import com.aiolos.live.common.keys.builder.RedisKeyBuilder;

public class MsgProviderCommonRedisKeyBuilder extends RedisKeyBuilder {

    private static final String SMS_LOGIN_CODE_KEY = "sms:loginCode";
    
    public MsgProviderCommonRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }

    public String buildSmsLoginCodeKey(String phone) {
        return SMS_LOGIN_CODE_KEY + super.getSplit() + phone;
    }
}
