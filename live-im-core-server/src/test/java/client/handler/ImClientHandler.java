package client.handler;

import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.common.ImMsgDecoder;
import com.aiolos.live.im.core.server.common.ImMsgEncoder;
import com.aiolos.live.im.interfaces.constants.AppIdEnum;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.interfaces.interfaces.ImTokenRpc;
import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImClientHandler implements InitializingBean {
    
    @DubboReference
    private ImTokenRpc imTokenRpc;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            EventLoopGroup clientGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ImMsgDecoder());
                    socketChannel.pipeline().addLast(new ImMsgEncoder());
                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });

            Map<Long, Channel> userChannelMap = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                ChannelFuture channelFuture = null;
                try {
                    channelFuture = bootstrap.connect("127.0.0.1", 9990).sync();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Channel channel = channelFuture.channel();
                
                ImMsgBody  body = new ImMsgBody();
                body.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
                body.setUserId(200000L);
                body.setToken(imTokenRpc.createImLoginToken(body.getUserId(), body.getAppId()));
                channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(body)));
                userChannelMap.put(body.getUserId(), channel);
            }

            while (true) {
                for (Long userId : userChannelMap.keySet()) {
                    ImMsgBody body = new ImMsgBody();
                    body.setUserId(userId);
                    body.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
                    userChannelMap.get(userId).writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(body)));
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
