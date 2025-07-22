package com.aiolos.live.im.core.server.mq.consumer;

import com.aiolos.live.im.core.server.service.MsgAckService;
import com.aiolos.live.im.core.server.service.RouterHandlerService;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.mq.message.ImAckDelayMessage;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class ImMsgConsumer {

    @Autowired
    private MsgAckService msgAckService;
    @Resource
    private RouterHandlerService routerHandlerService;
    
    @Bean
    public Consumer<ImAckDelayMessage> imAckDelayMsg() {
        return message -> {
            ImMsgBody imMsgBody = JSON.parseObject(message.getBodyJson(), ImMsgBody.class);
            int times = msgAckService.getMsgAckTimes(imMsgBody.getMsgId(), imMsgBody.getUserId(), imMsgBody.getAppId());
            if (times < 0) {
                // 已经ack了
            } else if (times < 2) {
                // 只重发一次，理想情况从发送延迟消息到进到这块逻辑期间客户端已经发送ack消息到服务端了，会将redis中的ack记录删除
                msgAckService.recordMsgAck(imMsgBody, times + 1);
                msgAckService.sendDelayMsg(imMsgBody);
                routerHandlerService.sendMsgToClient(imMsgBody);
            } else {
                log.warn("msgId[{}]重发次数已超过阈值", imMsgBody.getMsgId());
                // 删除需要ack的redis记录
                msgAckService.doMsgAck(imMsgBody);
            }
        };
    }
}
