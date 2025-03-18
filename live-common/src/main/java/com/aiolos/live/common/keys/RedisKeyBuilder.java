package com.aiolos.live.common.keys;

import java.util.concurrent.ThreadLocalRandom;

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

    public int randomExpireSeconds() {
        return ThreadLocalRandom.current().nextInt(60, 3600);
    }
}
