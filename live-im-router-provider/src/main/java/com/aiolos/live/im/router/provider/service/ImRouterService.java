package com.aiolos.live.im.router.provider.service;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;

import java.util.List;

public interface ImRouterService {
    
    void sendMsg(ImMsgBody imMsgBody);

    void batchSendMsg(List<ImMsgBody> imMsgBodies);
}
