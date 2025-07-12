package com.aiolos.live.im.core.server.rpc;

import com.aiolos.live.im.core.server.api.RouterHandlerApi;
import com.aiolos.live.im.core.server.service.RouterHandlerService;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
public class RouterHandlerApiImpl implements RouterHandlerApi {

    @Resource
    private RouterHandlerService routerHandlerService;
    
    @Override
    public void sendMsg(ImMsgBody imMsgBody) {
        routerHandlerService.receiveMsg(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> msgBodyList) {
        msgBodyList.forEach(msgBody -> routerHandlerService.receiveMsg(msgBody));
    }
}
