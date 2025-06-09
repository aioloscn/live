package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.ImHandlerFactory;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ImHandlerFactoryImpl implements ImHandlerFactory, InitializingBean {

    private static Map<Integer, SimpleHandler> handlerMap = new HashMap<>();
    
    @Resource
    private ApplicationContext applicationContext;
    
    @Override
    public void doMsgHandler(ChannelHandlerContext ctx, ImMsg msg) {
        SimpleHandler simpleHandler = handlerMap.get(msg.getCode());
        if (simpleHandler == null) {
            throw new IllegalArgumentException("error msg, msg is: " + msg);
        }
        simpleHandler.handle(ctx, msg);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 登录消息包，登录token认证，channel和userId关联
        handlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), applicationContext.getBean(LoginMsgHandler.class));
        // 登出消息包，正常断开im连接时发送
        handlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), applicationContext.getBean(LogoutMsgHandler.class));
        // 业务消息包，最常用的消息类型，发送数据、接收数据时用到
        handlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), applicationContext.getBean(BizMsgHandler.class));
        // 心跳消息包，定时给im发送，汇报功能
        handlerMap.put(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), applicationContext.getBean(HeartbeatMsgHandler.class)); 
        // ack消息包，确认收到消息
        handlerMap.put(ImMsgCodeEnum.IM_ACK_MSG.getCode(), applicationContext.getBean(AckMsgHandler.class));
    }
}
