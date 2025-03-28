package com.aiolos.live.common.keys;

public class UserProviderRedisKeyBuilder extends RedisKeyBuilder {

    private static final String USER_INFO_KEY = "userInfo";
    private static final String USER_TAG_LOCK_KEY = "userTagLock";
    private static final String USER_TAG_KEY = "userTag";
    private static final long EXPIRE_7_DAYS = 60 * 60 * 24 * 7;

    public UserProviderRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY + super.getSplit() + userId;
    }
    
    public String buildUserTagLockKey(Long userId) {
        return super.getPrefix() + USER_TAG_LOCK_KEY + super.getSplit() + userId;
    }
    
    public String buildUserTagKey(Long userId) {
        return super.getPrefix() + USER_TAG_KEY + super.getSplit() + userId;
    }

    public long get7DaysExpiration() {
        return EXPIRE_7_DAYS;
    }
}
