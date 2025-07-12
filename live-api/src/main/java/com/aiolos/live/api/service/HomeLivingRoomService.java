package com.aiolos.live.api.service;

import com.aiolos.live.api.bo.LivingRoomListBO;
import com.aiolos.live.api.vo.ApiLivingRoomVO;
import com.aiolos.live.api.vo.LivingRoomListVO;
import com.aiolos.common.wrapper.PageModel;
import com.aiolos.common.wrapper.PageResult;

public interface HomeLivingRoomService {

    /**
     * 开播
     * @param type 直播类型
     */
    Long startStreaming(Integer type);

    /**
     * 关播
     * @param roomId
     */
    void stopStreaming(Long roomId);

    /**
     * 查询直播列表
     * @param model
     * @return
     */
    PageResult<LivingRoomListVO> queryLivingRoomList(PageModel<LivingRoomListBO> model);

    /**
     * 查询直播间信息
     * @param userId
     * @param roomId
     */
    ApiLivingRoomVO queryByRoomId(Long userId, Long roomId);
}
