package com.aiolos.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存用户id相关的channel对象信息
 */
public class ChannelHandlerContextCache {
    
    private static String serverIpAddress;

    private static Map<Long, ChannelHandlerContext> channelHandlerContextMap = new HashMap<>();
    
    public static String getServerIpAddress() {
        return serverIpAddress;
    }
    
    public static void setServerIpAddress(String serverIpAddress) {
        ChannelHandlerContextCache.serverIpAddress = serverIpAddress;
    }

    public static ChannelHandlerContext get(Long userId) {
        return channelHandlerContextMap.get(userId);
    }

    public static void put(Long userId, ChannelHandlerContext ctx) {
        channelHandlerContextMap.put(userId, ctx);
    }
    
    public static void remove(Long userId) {
        channelHandlerContextMap.remove(userId);
    }
}
