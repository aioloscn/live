package com.aiolos.live.im.core.server.service;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;

public interface MsgAckService {

    /**
     * 客户端发送ack包给服务端后，调用进行ack记录的移除
     * @param imMsgBody
     */
    void doMsgAck(ImMsgBody imMsgBody);

    /**
     * 记录下消息的ack和times
     * @param imMsgBody
     * @param times 重试次数
     */
    void recordMsgAck(ImMsgBody imMsgBody, int times);

    /**
     * 发送延迟消息，用于进行消息重试
     * @param imMsgBody
     */
    void sendDelayMsg(ImMsgBody imMsgBody);

    /**
     * 获取消息的ack重试次数
     * @param msgId
     * @param userId
     * @param appId
     * @return
     */
    int getMsgAckTimes(String msgId, Long userId, Integer appId);
}
