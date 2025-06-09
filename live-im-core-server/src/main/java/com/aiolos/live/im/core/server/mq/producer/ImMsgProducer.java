package com.aiolos.live.im.core.server.mq.producer;

import com.aiolos.live.common.constants.mq.ImRocketMQBindingNames;
import com.aiolos.live.common.message.ImAckDelayMessage;
import com.aiolos.live.common.message.ImBizMessage;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
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
        boolean sent = streamBridge.send(ImRocketMQBindingNames.IM_BIZ_MSG, MessageBuilder.withPayload(message).build());
        if (!sent)
            log.error("发送biz消息失败");
        else
            log.info("已发送biz消息");
    }

    /**
     * 发送ack延迟消息
     * @param body
     */
    public void sendAckDelayMsg(ImMsgBody body) {
        ImAckDelayMessage message = new ImAckDelayMessage();
        message.setBodyJson(JSON.toJSONString(body));
        boolean sent = streamBridge.send(ImRocketMQBindingNames.IM_ACK_DELAY_MSG,
                MessageBuilder.withPayload(message).setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, 2).build());
        if (!sent)
            log.error("发送ack延迟消息失败");
        else
            log.info("已发送ack延迟消息");
    }
}
