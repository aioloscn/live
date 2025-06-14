package com.aiolos.live.living.provider.service;

import com.aiolos.live.common.wrapper.PageModel;
import com.aiolos.live.common.wrapper.PageResult;
import com.aiolos.live.living.dto.LivingRoomListDTO;
import com.aiolos.live.living.dto.StreamingDTO;
import com.aiolos.live.living.vo.LivingRoomVO;

public interface LiveLivingRoomService {

    /**
     * 开启直播
     * @param dto
     * @return
     */
    boolean startStreaming(StreamingDTO dto);

    /**
     * 关播
     * @param dto
     */
    void stopStreaming(StreamingDTO dto);
    
    /**
     * 查询直播列表
     * @param model
     * @return
     */
    PageResult<LivingRoomVO> queryLivingRoomList(PageModel<LivingRoomListDTO> model);
}
