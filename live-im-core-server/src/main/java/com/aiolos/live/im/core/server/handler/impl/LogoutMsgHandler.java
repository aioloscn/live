package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.common.message.ImOfflineMessage;
import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import com.aiolos.live.im.core.server.mq.producer.ImMsgProducer;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogoutMsgHandler implements SimpleHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder;
    @Autowired
    private ImMsgProducer imMsgProducer;
    
    @Override
    public void handle(ChannelHandlerContext ctx, ImMsg msg) {
        Long userId = ImContextUtil.getUserId(ctx);
        Integer appId = ImContextUtil.getAppId(ctx);
        if (userId == null || appId == null) {
            ctx.close();
            throw new IllegalArgumentException("attr data missing");
        }
        
        // 将im消息回写给客户端
        this.logoutHandler(ctx, userId, appId);
        this.sendOfflineMQ(ctx, userId, appId);
    }

    private void logoutHandler(ChannelHandlerContext ctx, Long userId, Integer appId) {
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody)));
        log.info("logout successfully, userId: {}, appId: {}", userId, appId);

        // 理想情况下，客户端断线时会发送一个断线消息包
        ChannelHandlerContextCache.remove(userId);
        stringRedisTemplate.delete(imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(appId, userId));
        ImContextUtil.removeUserId(ctx);
        ImContextUtil.removeAppId(ctx);
        ctx.close();
    }

    private void sendOfflineMQ(ChannelHandlerContext ctx, Long userId, Integer appId) {
        ImOfflineMessage message = new ImOfflineMessage();
        message.setUserId(userId);
        message.setAppId(appId);
        message.setRoomId(ImContextUtil.getRoomId(ctx));
        message.setLogoutTime(System.currentTimeMillis());
        imMsgProducer.sendOfflineMsg(message);
    }
}
