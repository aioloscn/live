package com.aiolos.live.user.provider.mq.producer;

import com.aiolos.live.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateUserInfoProducer {

    private final StreamBridge streamBridge;
    
    public UpdateUserInfoProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void deleteUserCache(UserDTO userDTO) {
        boolean sent = streamBridge.send("cacheAsyncDelete-out-0", 
                MessageBuilder.withPayload(userDTO).setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, 5).build());
        if (!sent) {
            log.error("发送删除用户{}缓存的延迟消息失败", userDTO.getUserId());
        }
        log.info("已发送删除用户{}缓存的延迟消息", userDTO.getUserId());
    }
}
