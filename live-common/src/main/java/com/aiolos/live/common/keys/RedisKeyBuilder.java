package com.aiolos.live.common.keys;

public class RedisKeyBuilder {

    private final String applicationName;
    private static final String SPLIT = ":";

    public RedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        this.applicationName = redisKeyProperties.getApplicationName();
    }

    public String getSplit() {
        return SPLIT;
    }

    public String getPrefix() {
        return applicationName + SPLIT;
    }
}
