package com.aiolos.live.im.core.server.handler.processor;

import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.coreflow.AbstractImHandlerProcessor;
import com.aiolos.live.im.core.server.mq.producer.ImMsgProducer;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BizImHandlerProcessor extends AbstractImHandlerProcessor {

    @Autowired
    private ImMsgProducer imMsgProducer;
    
    @Override
    public ImMsgCodeEnum getEnum() {
        return ImMsgCodeEnum.IM_BIZ_MSG;
    }

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
