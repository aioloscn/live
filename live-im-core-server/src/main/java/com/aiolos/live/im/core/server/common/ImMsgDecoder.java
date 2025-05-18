package com.aiolos.live.im.core.server.common;

import com.aiolos.live.im.interfaces.constants.ImConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 消息解码器
 */
public class ImMsgDecoder extends ByteToMessageDecoder {
    
    private static final int BASE_LEN = 2 + 4 + 4;  // short + int + int
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        // byteBuf内容的基本校验，长度校验，magic值校验
        if (byteBuf.readableBytes() >= BASE_LEN) {
            if (byteBuf.readShort() != ImConstants.DEFAULT_MAGIC) {
                ctx.close();
                return;
            }
            // 读取顺序取决于ImMsgEncoder中写入的顺序
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            // 剩余可读内容(body)长度
            if (byteBuf.readableBytes() < len) {
                ctx.close();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            // 交给下游处理器
            list.add(new ImMsg(ImConstants.DEFAULT_MAGIC, code, len, body));
        }
    }
}
