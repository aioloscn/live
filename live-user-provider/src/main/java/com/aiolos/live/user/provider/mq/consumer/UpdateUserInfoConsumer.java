package com.aiolos.live.user.provider.mq.consumer;

import com.aiolos.live.common.keys.UserProviderRedisKeyBuilder;
import com.aiolos.live.user.dto.UserDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class UpdateUserInfoConsumer {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserProviderRedisKeyBuilder userProviderRedisKeyBuilder;

    @Bean
    public Consumer<UserDTO> cacheAsyncDelete() {
        return message -> {
            log.info("接收到删除redis缓存消息: {}", message);
            if (message != null && message.getUserId() != null) {
                redisTemplate.delete(userProviderRedisKeyBuilder.buildUserInfoKey(message.getUserId()));
                log.info("已删除用户{}的缓存", message.getUserId());
            }
        };
    }
}
