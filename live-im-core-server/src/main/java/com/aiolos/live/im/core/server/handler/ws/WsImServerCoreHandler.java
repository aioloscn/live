package com.aiolos.live.im.core.server.handler.ws;

import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.coreflow.ImHandlerManager;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class WsImServerCoreHandler extends SimpleChannelInboundHandler {

    @Autowired
    private ImHandlerManager imHandlerManager;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder;
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            wsMsgHandler(ctx, (WebSocketFrame) msg);
        }
    }

    private void wsMsgHandler(ChannelHandlerContext ctx, WebSocketFrame msg) {
        // 不是文本消息不处理
        if (!(msg instanceof TextWebSocketFrame)) {
            log.error("[WebsocketCoreHandler]  wsMsgHandler , {} msg types not supported", msg.getClass().getName());
            return;
        }
        try {
            String content = ((TextWebSocketFrame) msg).text();
            JSONObject jsonObject = JSON.parseObject(content, JSONObject.class);
            ImMsg imMsg = new ImMsg();
            imMsg.setMagic(jsonObject.getShort("magic"));
            imMsg.setCode(jsonObject.getInteger("code"));
            imMsg.setLen(jsonObject.getInteger("len"));
            imMsg.setBody(jsonObject.getString("body").getBytes());
            imHandlerManager.getProcessor(ImMsgCodeEnum.getEnumByCode(imMsg.getCode())).handle(ctx, imMsg);
        } catch (Exception e) {
            log.error("[WebsocketCoreHandler]  wsMsgHandler , {}", e.getMessage(), e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ImContextUtil.getUserId(ctx);
        Integer appId = ImContextUtil.getAppId(ctx);
        if (userId != null && appId != null) {
            ChannelHandlerContextCache.remove(userId);
            stringRedisTemplate.delete(imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(appId, userId)); 
        }
    }
}
