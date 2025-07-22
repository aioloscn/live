package com.aiolos.live.living.provider.rpc;

import com.aiolos.live.living.dto.LivingRoomUserDTO;
import com.aiolos.live.living.interfaces.LivingRoomRpc;
import com.aiolos.live.living.provider.service.LiveLivingRoomService;
import com.aiolos.live.living.vo.LivingRoomUserVO;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class LivingRoomRpcImpl implements LivingRoomRpc {

    @Resource
    private LiveLivingRoomService liveLivingRoomService;
    
    @Override
    public LivingRoomUserVO queryLivingRoomUser(LivingRoomUserDTO dto) {
        return liveLivingRoomService.queryLivingRoomUser(dto);
    }
}
