package com.aiolos.live.im.core.server.mq.producer;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.mq.message.ImAckDelayMessage;
import com.aiolos.live.mq.message.ImBizMessage;
import com.aiolos.live.mq.message.ImOfflineMessage;
import com.aiolos.live.mq.message.ImOnlineMessage;
import com.aiolos.live.mq.topic.ImTopic;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ImMsgProducer {

    private final StreamBridge streamBridge;

    /**
     * 发送给专门处理msg的模块
     * @param body  就是ImMsg中的ImMsgBody
     */
    public void sendBizMsg(byte[] body) {
        ImBizMessage message = new ImBizMessage();
        message.setBody(body);
        boolean sent = streamBridge.send(ImTopic.IM_BIZ_MSG, MessageBuilder.withPayload(message).build());
        if (sent)
            log.info("已发送biz消息");
        else
            log.error("发送biz消息失败");
    }

    /**
     * 发送ack延迟消息
     * @param body
     */
    public void sendAckDelayMsg(ImMsgBody body) {
        ImAckDelayMessage message = new ImAckDelayMessage();
        message.setBodyJson(JSON.toJSONString(body));
        boolean sent = streamBridge.send(ImTopic.IM_ACK_DELAY_MSG,
                MessageBuilder.withPayload(message).setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, 2).build());
        if (sent)
            log.info("已发送ack延迟消息");
        else
            log.error("发送ack延迟消息失败");
    }

    public void sendOnlineMsg(ImOnlineMessage message) {
        boolean sent = streamBridge.send(ImTopic.IM_ONLINE_MSG, MessageBuilder.withPayload(message).build());
        if (sent)
            log.info("已发送im登录消息");
        else
            log.error("发送im登录消息失败");
    }
    
    public void sendOfflineMsg(ImOfflineMessage message) {
        boolean sent = streamBridge.send(ImTopic.IM_OFFLINE_MSG, MessageBuilder.withPayload(message).build());
        if (sent)
            log.info("已发送im登出消息");
        else
            log.error("发送im登出消息失败");
    }
}
