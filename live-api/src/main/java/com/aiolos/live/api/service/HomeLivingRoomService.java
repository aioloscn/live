package com.aiolos.live.api.service;

import com.aiolos.live.api.bo.LivingRoomListBO;
import com.aiolos.live.api.vo.LivingRoomListVO;
import com.aiolos.live.common.wrapper.PageModel;
import com.aiolos.live.common.wrapper.PageResult;

public interface HomeLivingRoomService {

    /**
     * 开播
     * @param type 直播类型
     */
    boolean startStreaming(Integer type);

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
}
