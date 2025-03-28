package com.aiolos.live.user.provider.mq.producer;

import com.aiolos.live.common.message.UserCacheMessage;
import com.aiolos.live.enums.UserCacheEnum;
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

    public void deleteUserCache(Long userId) {
        UserCacheMessage message = new UserCacheMessage();
        message.setUserId(userId);
        message.setUserCacheEnum(UserCacheEnum.USER_INFO_CACHE);
        boolean sent = streamBridge.send("cacheAsyncDelete-out-0", 
                MessageBuilder.withPayload(message).setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, 1).build());
        if (!sent) {
            log.error("发送删除用户{}缓存的延迟消息失败", userId);
        }
        log.info("已发送删除用户{}缓存的延迟消息", userId);
    }

    public void deleteUserTagCache(Long userId) {
        UserCacheMessage message = new UserCacheMessage();
        message.setUserId(userId);
        message.setUserCacheEnum(UserCacheEnum.USER_TAG_CACHE);
        boolean sent = streamBridge.send("cacheAsyncDelete-out-0",
                MessageBuilder.withPayload(message).setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, 1).build());
        if (!sent) {
            log.error("发送删除用户{}tag缓存的延迟消息失败", userId);
        }
        log.info("已发送删除用户{}tag缓存的延迟消息", userId);
    }
}
