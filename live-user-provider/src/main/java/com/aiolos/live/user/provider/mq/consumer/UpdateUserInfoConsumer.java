package com.aiolos.live.user.provider.mq.consumer;

import com.aiolos.live.common.keys.builder.common.UserProviderCommonRedisKeyBuilder;
import com.aiolos.live.common.message.UserCacheMessage;
import com.aiolos.live.enums.UserCacheEnum;
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
    private UserProviderCommonRedisKeyBuilder userProviderCommonRedisKeyBuilder;

    @Bean
    public Consumer<UserCacheMessage> cacheAsyncDelete() {
        return message -> {
            log.info("接收到删除redis缓存消息: {}", message);
            if (message != null && message.getUserId() != null) {
                
                if (message.getUserCacheEnum() == UserCacheEnum.USER_INFO_CACHE) {
                    redisTemplate.delete(userProviderCommonRedisKeyBuilder.buildUserInfoKey(message.getUserId()));
                    log.info("已删除用户{}的缓存", message.getUserId());
                } else if (message.getUserCacheEnum() == UserCacheEnum.USER_TAG_CACHE) {
                    redisTemplate.delete(userProviderCommonRedisKeyBuilder.buildUserTagKey(message.getUserId()));
                    log.info("已删除用户{}的标签缓存", message.getUserId());
                }
            }
        };
    }
}
