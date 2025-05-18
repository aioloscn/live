package client.handler;

import com.aiolos.live.im.core.server.common.ImMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        ImMsg msg = (ImMsg) o;
        System.out.println("client receive msg: " + msg);
    }
}
