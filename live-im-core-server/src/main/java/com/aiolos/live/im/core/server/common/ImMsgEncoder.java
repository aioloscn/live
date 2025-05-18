package com.aiolos.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 处理消息的编码
 */
public class ImMsgEncoder extends MessageToByteEncoder {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
        ImMsg msg = (ImMsg) o;
        byteBuf.writeShort(msg.getMagic());
        byteBuf.writeInt(msg.getCode());
        byteBuf.writeInt(msg.getLen());
        byteBuf.writeBytes(msg.getBody());
    }
}
