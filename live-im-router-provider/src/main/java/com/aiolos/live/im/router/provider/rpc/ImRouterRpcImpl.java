package com.aiolos.live.im.router.provider.rpc;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.router.interfaces.ImRouterRpc;
import com.aiolos.live.im.router.provider.service.ImRouterService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService imRouterService;
    
    @Override
    public void sendMsg(ImMsgBody imMsgBody) {
        imRouterService.sendMsg(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodies) {
        imRouterService.batchSendMsg(imMsgBodies);
    }
}
