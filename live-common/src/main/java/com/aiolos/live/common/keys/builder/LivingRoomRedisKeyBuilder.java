package com.aiolos.live.common.keys.builder;

import com.aiolos.live.common.keys.RedisKeyProperties;

public class LivingRoomRedisKeyBuilder extends RedisKeyBuilder {

    private final static String LIVING_ROOM_OBJ_KEY = "livingRoomObj";
    private final static String LIVING_ROOM_LIST_KEY = "livingRoomList";
    
    public LivingRoomRedisKeyBuilder(RedisKeyProperties redisKeyProperties) {
        super(redisKeyProperties);
    }
    
    public String buildLivingRoomObjKey(Long roomId) {
        return getPrefix() + LIVING_ROOM_OBJ_KEY + getSplit() + roomId;
    }
    
    public String buildLivingRoomListKey(Integer type) {
        return getPrefix() + LIVING_ROOM_LIST_KEY + getSplit() + type;
    }
}
