package com.aiolos.live.common.keys;

public class UserProviderRedisKeyBuilder extends RedisKeyBuilder {

    private static final String KEY = "userInfo";
    private static final long EXPIRE = 60 * 60 * 24 * 7;

    public UserProviderRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + KEY + super.getSplit() + userId;
    }

    public long getExpire() {
        return EXPIRE;
    }
}
