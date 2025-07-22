package com.aiolos.live.living.provider.service;

import com.aiolos.common.wrapper.PageModel;
import com.aiolos.common.wrapper.PageResult;
import com.aiolos.live.living.dto.LivingRoomUserDTO;
import com.aiolos.live.living.provider.bo.LivingRoomListBO;
import com.aiolos.live.living.provider.bo.StreamingBO;
import com.aiolos.live.living.vo.LivingRoomUserVO;
import com.aiolos.live.living.vo.LivingRoomVO;

public interface LiveLivingRoomService {

    /**
     * 开启直播
     * @param bo
     * @return
     */
    Long startStreaming(StreamingBO bo);

    /**
     * 关播
     * @param bo
     */
    void stopStreaming(StreamingBO bo);
    
    /**
     * 查询直播列表
     * @param model
     * @return
     */
    PageResult<LivingRoomVO> queryLivingRoomList(PageModel<LivingRoomListBO> model);
    
    LivingRoomVO queryByRoomId(Long roomId);

    /**
     * 根据房间id查询用户列表
     * @param dto
     * @return
     */
    LivingRoomUserVO queryLivingRoomUser(LivingRoomUserDTO dto);
}
