package com.aiolos.live.im.core.server.handler;

import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 接收外界消息处理器
 */
@Service
@ChannelHandler.Sharable    // 需确保线程安全
public class ImServerCoreHandler extends SimpleChannelInboundHandler {

    @Resource
    private ImHandlerFactory imHandlerFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        if (!(o instanceof ImMsg msg)) {
            throw new IllegalArgumentException("error msg, msg is: " + o);
        }
        imHandlerFactory.doMsgHandler(ctx, msg);
    }

    /**
     * 正常或意外断开连接都会触发这个函数
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ImContextUtil.getUserId(ctx);
        if (userId != null) {
            ChannelHandlerContextCache.remove(userId);
        }
    }
}
