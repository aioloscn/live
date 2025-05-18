package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import com.aiolos.live.im.interfaces.constants.AppIdEnum;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.interfaces.interfaces.ImTokenRpc;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginMsgHandler implements SimpleHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    
    @Override
    public void handle(ChannelHandlerContext ctx, ImMsg msg) {
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

            // 保存用户id相关的channel对象信息
            ChannelHandlerContextCache.put(userId, ctx);
            ImContextUtil.setUserId(ctx, userId);
            
            ImMsgBody respBody = new ImMsgBody();
            respBody.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
            respBody.setUserId(userId);
            respBody.setData("true");
            ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
            // 回写回给客户端
            ctx.writeAndFlush(respMsg);
            log.info("login successfully, userId: {}, appId: {}", userId, appId);
            return;
        }

        ctx.close();
        throw new IllegalArgumentException("token invalid");
    }
}
