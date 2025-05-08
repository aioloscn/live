package com.aiolos.live.common.keys.builder;

import com.aiolos.live.common.keys.RedisKeyProperties;

public class UserProviderRedisKeyBuilder extends RedisKeyBuilder {

    private static final String USER_INFO_KEY = "userInfo";
    private static final String USER_TAG_LOCK_KEY = "userTagLock";
    private static final String USER_TAG_KEY = "userTag";
    private static final String USER_TOKEN_KEY = "userToken";
    private static final String USER_PHONE_KEY = "userPhone";

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
    
    public String buildUserTokenKey(String token) {
        return super.getPrefix() + USER_TOKEN_KEY + super.getSplit() + token;
    }
    
    public String buildUserPhoneKey(String phone) {
        return super.getPrefix() + USER_PHONE_KEY + super.getSplit() + phone;
    }
}
