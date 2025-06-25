package com.aiolos.live.im.core.server.handler.processor;

import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.coreflow.AbstractImHandlerProcessor;
import com.aiolos.live.im.core.server.service.MsgAckService;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class AckMsgHandlerProcessor extends AbstractImHandlerProcessor {

    @Resource
    private MsgAckService msgAckService;
    
    @Override
    public ImMsgCodeEnum getEnum() {
        return ImMsgCodeEnum.IM_ACK_MSG;
    }

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
