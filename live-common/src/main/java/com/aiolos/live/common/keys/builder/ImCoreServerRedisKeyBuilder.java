package com.aiolos.live.common.keys.builder;

import com.aiolos.live.common.keys.RedisKeyProperties;

public class ImCoreServerRedisKeyBuilder extends RedisKeyBuilder {

    private static final String IM_ONLINE_ZET_KEY = "im:onlineZSet";
    
    public ImCoreServerRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }

    /**
     * 用userId对10000取模，得出具体缓存所在的key
     * @param userId
     * @return
     */
    public String buildImOnlineZSetKey(Long userId, Integer appId) {
        return getPrefix() + IM_ONLINE_ZET_KEY + getSplit() + appId + getSplit() + userId % 10000;
    }
}
