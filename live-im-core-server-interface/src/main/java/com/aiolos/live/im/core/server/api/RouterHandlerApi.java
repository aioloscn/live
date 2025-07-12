package com.aiolos.live.im.core.server.api;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;

import java.util.List;

public interface RouterHandlerApi {

    /**
     * 按照用户id进行发送消息
     * @param imMsgBody
     */
    void sendMsg(ImMsgBody imMsgBody);

    /**
     * 向同一个节点的用户发送消息
     * @param msgBodyList
     */
    void batchSendMsg(List<ImMsgBody> msgBodyList);
}
