package com.aiolos.live.im.core.server.handler;

import com.aiolos.live.im.core.server.common.ImMsg;
import io.netty.channel.ChannelHandlerContext;

public interface SimpleHandler {

    void handle(ChannelHandlerContext ctx, ImMsg msg);
}
