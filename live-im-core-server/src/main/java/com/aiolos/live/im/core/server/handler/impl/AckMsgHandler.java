package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import com.aiolos.live.im.core.server.service.MsgAckService;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AckMsgHandler implements SimpleHandler {

    @Resource
    private MsgAckService msgAckService;
    
    @Override
    public void handle(ChannelHandlerContext ctx, ImMsg msg) {
        Long userId = ImContextUtil.getUserId(ctx);
        Integer appId = ImContextUtil.getAppId(ctx);
        if (userId == null || appId == null) {
            ctx.close();
            throw new IllegalArgumentException("attr data missing");
        }
        msgAckService.doMsgAck(JSON.parseObject(msg.getBody(), ImMsgBody.class));
    }
}
