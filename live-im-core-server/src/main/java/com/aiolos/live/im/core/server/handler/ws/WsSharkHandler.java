package com.aiolos.live.im.core.server.handler.ws;

import com.aiolos.live.im.core.server.handler.impl.LoginMsgHandler;
import com.aiolos.live.im.interfaces.interfaces.ImTokenRpc;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@ChannelHandler.Sharable
public class WsSharkHandler extends ChannelInboundHandlerAdapter {
    
    @Value("${im.ws.port}")
    private int port;
    @Value("${config.ws.ip}")
    private String serverIp;
    @Resource
    private ImTokenRpc imTokenRpc;
    @Resource
    private LoginMsgHandler loginMsgHandler;

    private WebSocketServerHandshaker webSocketServerHandshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 握手接入ws
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        
        // 正常关闭链路
        if (msg instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), ((CloseWebSocketFrame) msg).retain());
            return;
        }
        // 传递给ChannelPipeline中的下一个处理器
        ctx.fireChannelRead(msg);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        String webSocketUrl = "ws://" + serverIp + ":" + port;

        Long userId = null, queryUserId = null;
        Integer appId = null;
        String selectedProtocol = null;
        String authData = null;
        
        String subProtocols = request.headers().get("Sec-WebSocket-Protocol");
        if (subProtocols != null) {
            List<String> protocols = Arrays.asList(subProtocols.split(",\\s*"));

            if (protocols.contains("auth-protocol")) {
                selectedProtocol = "auth-protocol";

                for (String protocol : protocols) {
                    if (!"auth-protocol".equals(protocol)) {
                        authData = protocol;
                        break;
                    }
                }
            }

            if (authData == null) {
                sendUnauthorizedResponse(ctx);
                return;
            }
            
            // 子协议中不能包含某些符号，需要恢复
            try {
                // 恢复标准 Base64 格式
                String standardBase64 = authData
                        .replace('-', '+')
                        .replace('_', '/');

                // 添加填充字符（如果需要）
                switch (standardBase64.length() % 4) {
                    case 2: standardBase64 += "=="; break;
                    case 3: standardBase64 += "="; break;
                }

                byte[] decoded = Base64.getDecoder().decode(standardBase64);
                JSONObject authInfo = JSON.parseObject(new String(decoded, StandardCharsets.UTF_8));

                // 提取认证信息
                String token = authInfo.getString("token");
                userId = authInfo.getLong("userId");
                queryUserId = imTokenRpc.getUserIdByToken(token);
                appId = Integer.valueOf(token.substring(token.lastIndexOf("%23") + 3));

                if (userId == null || !userId.equals(queryUserId)) {
                    log.error("[WsSharkHandler] token verification failed");
                    sendUnauthorizedResponse(ctx);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendUnauthorizedResponse(ctx);
                return;
            }
        }
        
        // 用子协议创建握手器
        webSocketServerHandshaker = new WebSocketServerHandshakerFactory(webSocketUrl, selectedProtocol, false).newHandshaker(request);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }

        ChannelFuture channelFuture = webSocketServerHandshaker.handshake(ctx.channel(), request);
        // 首次握手建立ws连接后返回消息给客户端
        if (channelFuture.isSuccess()) {
            loginMsgHandler.loginSuccessHandler(ctx, userId, appId);
            log.info("[WebSocketSharkHandler] channel is connect! user is {}", userId);
        }
    }

    private void sendUnauthorizedResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.UNAUTHORIZED
        );
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
