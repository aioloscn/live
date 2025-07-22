package com.aiolos.live.living.interfaces;

import com.aiolos.live.living.dto.LivingRoomUserDTO;
import com.aiolos.live.living.vo.LivingRoomUserVO;

public interface LivingRoomRpc {

    /**
     * 根据房间id查询用户列表
     * @param dto
     * @return
     */
    LivingRoomUserVO queryLivingRoomUser(LivingRoomUserDTO dto);
}
