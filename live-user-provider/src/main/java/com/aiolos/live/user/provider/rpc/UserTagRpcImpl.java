package com.aiolos.live.user.provider.rpc;

import com.aiolos.live.enums.UserTagEnum;
import com.aiolos.live.user.interfaces.UserTagRpc;
import com.aiolos.live.user.provider.service.LiveUserTagService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserTagRpcImpl implements UserTagRpc {
    
    @Resource
    private LiveUserTagService liveUserTagService;
    
    @Override
    public boolean setTag(Long userId, UserTagEnum userTagEnum) {
        return liveUserTagService.setTag(userId, userTagEnum);
    }

    @Override
    public boolean cancelTag(Long userId, UserTagEnum userTagEnum) {
        return liveUserTagService.cancelTag(userId, userTagEnum);
    }

    @Override
    public boolean checkTag(Long userId, UserTagEnum userTagEnum) {
        return liveUserTagService.checkTag(userId, userTagEnum);
    }
}
