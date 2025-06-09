package com.aiolos.live.im.core.server.rpc;

import com.aiolos.live.im.core.server.dto.RouterHandlerMsgDTO;
import com.aiolos.live.im.core.server.interfaces.RouterHandlerRpc;
import com.aiolos.live.im.core.server.service.RouterHandlerService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class RouterHandlerRpcImpl implements RouterHandlerRpc {

    @Resource
    private RouterHandlerService routerHandlerService;
    
    @Override
    public void sendMsg(RouterHandlerMsgDTO dto) {
        routerHandlerService.receiveMsg(dto);
    }
}
