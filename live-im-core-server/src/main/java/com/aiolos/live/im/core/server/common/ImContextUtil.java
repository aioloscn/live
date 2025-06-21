package com.aiolos.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

/**
 * 通过ChannelHandlerContext的attr方法绑定/获取一些业务属性
 */
public class ImContextUtil {
    
    public static void setUserId(ChannelHandlerContext ctx, Long userId) {
        ctx.channel().attr(ImContextAttr.USER_ID).set(userId);
    }
    
    public static Long getUserId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ImContextAttr.USER_ID).get();
    }

    public static void removeUserId(ChannelHandlerContext ctx) {
        ctx.channel().attr(ImContextAttr.USER_ID).remove();
    }
    
    public static void setAppId(ChannelHandlerContext ctx, Integer appId) {
        ctx.channel().attr(ImContextAttr.APP_ID).set(appId);
    }
    
    public static Integer getAppId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ImContextAttr.APP_ID).get();
    }
    
    public static void removeAppId(ChannelHandlerContext ctx) {
        ctx.channel().attr(ImContextAttr.APP_ID).remove();
    }
    
    public static void setRoomId(ChannelHandlerContext ctx, Long roomId) {
        ctx.channel().attr(ImContextAttr.ROOM_ID).set(roomId);
    }
    
    public static Long getRoomId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ImContextAttr.ROOM_ID).get();
    }
    
    public static void removeRoomId(ChannelHandlerContext ctx) {
        ctx.channel().attr(ImContextAttr.ROOM_ID).remove();
    }
}
