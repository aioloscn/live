package client.handler;

import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        ImMsg msg = (ImMsg) o;
        System.out.println("client receive msg: " + new String(msg.getBody()));
        if (msg.getCode() == ImMsgCodeEnum.IM_BIZ_MSG.getCode()) {
            ImMsgBody respMsgBody = JSON.parseObject(new String(msg.getBody()), ImMsgBody.class);
            ImMsgBody ackMsgBody = new ImMsgBody();
            ackMsgBody.setMsgId(respMsgBody.getMsgId());
            ackMsgBody.setUserId(respMsgBody.getUserId());
            ackMsgBody.setAppId(respMsgBody.getAppId());
            ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_ACK_MSG.getCode(), JSON.toJSONString(ackMsgBody)));
        }
    }
}
