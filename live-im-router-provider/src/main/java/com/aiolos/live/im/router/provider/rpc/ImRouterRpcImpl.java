package com.aiolos.live.im.router.provider.rpc;

import com.aiolos.live.im.router.dto.RouterMsgDTO;
import com.aiolos.live.im.router.interfaces.ImRouterRpc;
import com.aiolos.live.im.router.provider.service.ImRouterService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService imRouterService;
    
    @Override
    public void sendMsg(RouterMsgDTO dto) {
        imRouterService.sendMsg(dto);
    }
}
