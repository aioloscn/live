package com.aiolos.live.living.interfaces;

import com.aiolos.live.common.wrapper.PageModel;
import com.aiolos.live.common.wrapper.PageResult;
import com.aiolos.live.living.dto.LivingRoomListDTO;
import com.aiolos.live.living.dto.StreamingDTO;
import com.aiolos.live.living.vo.LivingRoomVO;

public interface LivingRoomRpc {

    Long startStreaming(StreamingDTO dto);

    void stopStreaming(StreamingDTO dto);
    
    PageResult<LivingRoomVO> queryLivingRoomList(PageModel<LivingRoomListDTO> model);
    
    LivingRoomVO queryByRoomId(Long roomId);
}
