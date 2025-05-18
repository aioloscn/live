package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BizMsgHandler implements SimpleHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, ImMsg msg) {
        log.info("[biz]: {}", msg);
        ctx.writeAndFlush(msg);
    }
}
