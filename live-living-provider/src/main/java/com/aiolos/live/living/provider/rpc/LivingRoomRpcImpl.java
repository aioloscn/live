package com.aiolos.live.living.provider.rpc;

import com.aiolos.live.common.wrapper.PageModel;
import com.aiolos.live.common.wrapper.PageResult;
import com.aiolos.live.living.dto.LivingRoomListDTO;
import com.aiolos.live.living.dto.LivingRoomUserDTO;
import com.aiolos.live.living.dto.StreamingDTO;
import com.aiolos.live.living.interfaces.LivingRoomRpc;
import com.aiolos.live.living.provider.service.LiveLivingRoomService;
import com.aiolos.live.living.vo.LivingRoomUserVO;
import com.aiolos.live.living.vo.LivingRoomVO;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class LivingRoomRpcImpl implements LivingRoomRpc {
    
    @Resource
    private LiveLivingRoomService liveLivingRoomService;
    
    @Override
    public Long startStreaming(StreamingDTO dto) {
        return liveLivingRoomService.startStreaming(dto);
    }

    @Override
    public void stopStreaming(StreamingDTO dto) {
        liveLivingRoomService.stopStreaming(dto);
    }

    @Override
    public PageResult<LivingRoomVO> queryLivingRoomList(PageModel<LivingRoomListDTO> model) {
        return liveLivingRoomService.queryLivingRoomList(model);
    }

    @Override
    public LivingRoomVO queryByRoomId(Long roomId) {
        return liveLivingRoomService.queryByRoomId(roomId);
    }

    @Override
    public LivingRoomUserVO queryLivingRoomUser(LivingRoomUserDTO dto) {
        return liveLivingRoomService.queryLivingRoomUser(dto);
    }
}
