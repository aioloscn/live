package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.common.message.ImOnlineMessage;
import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import com.aiolos.live.im.core.server.mq.producer.ImMsgProducer;
import com.aiolos.live.im.interfaces.constants.AppIdEnum;
import com.aiolos.live.im.interfaces.constants.ImConstants;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.interfaces.ImTokenRpc;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoginMsgHandler implements SimpleHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder;
    @Autowired
    private ImMsgProducer imMsgProducer;
    
    @Override
    public void handle(ChannelHandlerContext ctx, ImMsg msg) {
        if (ImContextUtil.getUserId(ctx) != null) {
            return;
        }
        
        byte[] body = msg.getBody();
        if (body == null || body.length == 0) {
            ctx.close();
            throw new IllegalArgumentException("msg body data missing");
        }
        
        ImMsgBody imMsgBody = JSON.parseObject(body, ImMsgBody.class);
        int appId = imMsgBody.getAppId();
        String token = imMsgBody.getToken();
        if (StringUtils.isBlank(token) || appId < 10000) {
            ctx.close();
            throw new IllegalArgumentException("msg token or appId is error"); 
        }
        
        Long userId = imTokenRpc.getUserIdByToken(token);
        if (userId != null && userId.equals(imMsgBody.getUserId())) {

            this.loginSuccessHandler(ctx, userId, appId, null);
            log.info("login successfully, userId: {}, appId: {}", userId, appId);
            return;
        }

        ctx.close();
        throw new IllegalArgumentException("token invalid");
    }

    public void loginSuccessHandler(ChannelHandlerContext ctx, Long userId, Integer appId, Long roomId) {
        // 保存用户id相关的channel对象信息
        ChannelHandlerContextCache.put(userId, ctx);
        ImContextUtil.setUserId(ctx, userId);
        ImContextUtil.setAppId(ctx, appId);
        // 只有握手成功才有roomId
        if (roomId != null) ImContextUtil.setRoomId(ctx, roomId);

        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
        respBody.setUserId(userId);

        JSONObject jsonObject = new JSONObject();
        if (userId > 0) {
            jsonObject.put("type", "NORMAL");
        } else {
            jsonObject.put("type", "ANONYMOUS");
        }
        respBody.setData(JSON.toJSONString(jsonObject));
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
        // 绑定用户登录的服务器所在ip
        stringRedisTemplate.opsForValue().set(imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(appId, userId),
                ChannelHandlerContextCache.getServerIpAddress() + "%" + userId, ImConstants.DEFAULT_HEARTBEAT_GAP * 2, TimeUnit.SECONDS);

        // 回写回给客户端
        ctx.writeAndFlush(respMsg);
        this.sendOnlineMQ(userId, appId, roomId);
    }

    private void sendOnlineMQ(Long userId, Integer appId, Long roomId) {
        ImOnlineMessage message = new ImOnlineMessage();
        message.setUserId(userId);
        message.setAppId(appId);
        message.setRoomId(roomId);
        message.setLoginTime(System.currentTimeMillis());
        imMsgProducer.sendOnlineMsg(message);
    }
}
