package com.aiolos.live.im.core.server.service;

import com.aiolos.live.im.core.server.dto.RouterHandlerMsgDTO;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;

public interface RouterHandlerService {

    /**
     * 接收到router转发过来的业务请求时进行处理
     * @param dto
     */
    void receiveMsg(RouterHandlerMsgDTO dto);

    /**
     * 回写给客户端
     * @param imMsgBody
     * @return
     */
    boolean sendMsgToClient(ImMsgBody imMsgBody);
}
