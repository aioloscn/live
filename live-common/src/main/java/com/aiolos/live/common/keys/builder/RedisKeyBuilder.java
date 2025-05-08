package com.aiolos.live.common.keys.builder;

import com.aiolos.live.common.keys.RedisKeyProperties;

import java.util.concurrent.ThreadLocalRandom;

public class RedisKeyBuilder {

    private final String applicationName;
    private static final String SPLIT = ":";
    private static final long EXPIRE_7_DAYS = 60 * 60 * 24 * 7;

    public RedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        this.applicationName = redisKeyProperties.getName();
    }

    public String getSplit() {
        return SPLIT;
    }

    public String getPrefix() {
        return applicationName + SPLIT;
    }

    public int randomExpireSeconds() {
        return ThreadLocalRandom.current().nextInt(60, 3600);
    }

    public long get7DaysExpiration() {
        return EXPIRE_7_DAYS;
    } 
}
