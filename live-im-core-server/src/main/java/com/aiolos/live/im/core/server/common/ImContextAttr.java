package com.aiolos.live.im.core.server.common;

import io.netty.util.AttributeKey;

public class ImContextAttr {

    /**
     * 绑定用户id
     */
    public static AttributeKey<Long> USER_ID = AttributeKey.valueOf("userId");
    
    public static AttributeKey<Integer> APP_ID = AttributeKey.valueOf("appId");
    
    public static AttributeKey<Long> ROOM_ID = AttributeKey.valueOf("roomId");
}
