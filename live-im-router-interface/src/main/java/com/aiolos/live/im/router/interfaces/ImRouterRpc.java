package com.aiolos.live.im.router.interfaces;

import com.aiolos.live.im.router.dto.RouterMsgDTO;

public interface ImRouterRpc {
    
    void sendMsg(RouterMsgDTO dto);
}
