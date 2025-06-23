package client.handler;

import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.common.TcpMsgDecoder;
import com.aiolos.live.im.core.server.common.TcpMsgEncoder;
import com.aiolos.live.im.interfaces.constants.AppIdEnum;
import com.aiolos.live.im.interfaces.constants.BizCodeEnum;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.interfaces.ImTokenRpc;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Service
public class ImClientHandler implements InitializingBean {
    
    @DubboReference
    private ImTokenRpc imTokenRpc;
    
    private Bootstrap bootstrap;
    Map<Long, Channel> userChannelMap = new HashMap<>();
    
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            EventLoopGroup clientGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(clientGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new TcpMsgDecoder());
                    socketChannel.pipeline().addLast(new TcpMsgEncoder());
                    socketChannel.pipeline().addLast(new ClientHandler());
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("连接断开，尝试重新连接...");
                            ctx.channel().eventLoop().schedule(() -> reconnect(ctx.channel().id()), 5, TimeUnit.SECONDS);
                            super.channelInactive(ctx);
                        }
                    });
                }
            });

            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("请输入userId");
                long userId = scanner.nextLong();
                System.out.println("请输入receiverId");
                long receiverId = scanner.nextLong();
                connect(userId);
                sendHeartbeat(userId);

                while (true) {
                    System.out.println("请输入聊天内容");
                    String content = scanner.nextLine();
                    if (StringUtils.isBlank(content))
                        continue;
                    ImMsgBody body = new ImMsgBody();
                    body.setUserId(userId);
                    body.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
                    body.setBizCode(BizCodeEnum.LIVING_CHAT_MSG.getCode());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", userId);
                    // 测试中一对一发送消息，后续没有receiverId，而是roomId，并且根据roomId拉取用户推送消息
                    jsonObject.put("receiverId", receiverId);
                    jsonObject.put("content", content);
                    body.setData(JSON.toJSONString(jsonObject));
                    userChannelMap.get(userId).writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(body)));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void connect(Long userId) throws Exception {
        ChannelFuture channelFuture = bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000).connect("127.0.0.1", 9990).sync();
        Channel channel = channelFuture.channel();

        ImMsgBody  body = new ImMsgBody();
        body.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
        body.setUserId(userId);
        body.setToken(imTokenRpc.createImLoginToken(body.getUserId(), body.getAppId()));
        channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(body)));
        userChannelMap.put(body.getUserId(), channel);
        System.out.println("用户" + userId + "重新连接成功");
    }

    public void reconnect(ChannelId channelId) {
        Channel channel = userChannelMap.values().stream().filter(c -> c.id().equals(channelId)).findFirst().orElse(null);
        for (Map.Entry<Long, Channel> entry : userChannelMap.entrySet()) {
            Long k = entry.getKey();
            Channel v = entry.getValue();
            if (v.id().equals(channelId)) {
                try {
                    connect(k);
                } catch (Exception e) {
                    if (e instanceof ConnectException) {
                        System.out.println("重新连接失败，稍后再次尝试");
                        channel.eventLoop().schedule(() -> reconnect(channelId), 5, TimeUnit.SECONDS);
                    } else {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    public void sendHeartbeat(Long userId) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ImMsgBody body = new ImMsgBody();
                body.setUserId(userId);
                body.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
                userChannelMap.get(userId).writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(body)));
            }
        }).start();
    }
}
