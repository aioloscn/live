package com.aiolos.live.im.core.server.interfaces;

import com.aiolos.live.im.core.server.dto.RouterHandlerMsgDTO;

public interface RouterHandlerRpc {

    /**
     * 按照用户id进行发送消息
     * @param dto
     */
    void sendMsg(RouterHandlerMsgDTO dto);
}
