package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import com.aiolos.live.im.core.server.mq.producer.ImMsgProducer;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BizMsgHandler implements SimpleHandler {

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

        byte[] body = msg.getBody();
        if (body == null || body.length == 0) {
            ctx.close();
            throw new IllegalArgumentException("msg body data missing");
        }
        
        imMsgProducer.sendBizMsg(body);
    }
}
