package com.aiolos.live.living.provider.mq.consumer;

import com.aiolos.live.common.keys.builder.LivingRoomRedisKeyBuilder;
import com.aiolos.live.common.message.ImOfflineMessage;
import com.aiolos.live.common.message.ImOnlineMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Component
public class ImMsgConsumer {

    @Resource
    private LivingRoomRedisKeyBuilder livingRoomRedisKeyBuilder;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Bean
    public Consumer<ImOnlineMessage> imOnlineMsg() {
        return message -> {
            String key = livingRoomRedisKeyBuilder.buildLivingRoomUserSetKey(message.getRoomId(), message.getAppId());
            // 建立直播间用户关系
            redisTemplate.opsForSet().add(key, message.getUserId());
            redisTemplate.expire(key, 12, TimeUnit.HOURS);
        };
    }
    
    @Bean
    public Consumer<ImOfflineMessage> imOfflineMsg() {
        return message -> {
            String key = livingRoomRedisKeyBuilder.buildLivingRoomUserSetKey(message.getRoomId(), message.getAppId());
            // 删除直播间用户关系
            redisTemplate.opsForSet().remove(key, message.getUserId());
        };
    }
}
