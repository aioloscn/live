package com.aiolos.live.im.core.server.handler.coreflow;

import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractImHandlerProcessor implements InitializingBean {

    @Resource
    private ImHandlerManager imHandlerManager;
    
    public abstract ImMsgCodeEnum getEnum();
    
    @Override
    public void afterPropertiesSet() throws Exception {
        imHandlerManager.register(getEnum(), this);
    }
    
    public abstract void handle(ChannelHandlerContext ctx, ImMsg msg);
}
