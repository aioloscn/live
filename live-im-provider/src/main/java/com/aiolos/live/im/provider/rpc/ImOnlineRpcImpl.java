package com.aiolos.live.im.provider.rpc;

import com.aiolos.live.im.interfaces.interfaces.ImOnlineRpc;
import com.aiolos.live.im.provider.service.ImOnlineService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {
    
    @Resource
    private ImOnlineService imOnlineService;
    
    @Override
    public boolean isOnline(Integer appId, Long userId) {
        return imOnlineService.isOnline(appId, userId);
    }
}
