package com.aiolos.live.im.router.interfaces;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;

import java.util.List;

public interface ImRouterRpc {
    
    void sendMsg(ImMsgBody imMsgBody);

    void batchSendMsg(List<ImMsgBody> imMsgBodies);
}
