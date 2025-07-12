package com.aiolos.live.common.config;

import com.aiolos.live.common.keys.*;
import com.aiolos.live.common.keys.builder.*;
import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.common.keys.builder.common.MsgProviderCommonRedisKeyBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisKeyProperties.class)
public class CommonRedisAutoConfiguration {

    @Bean
    public RedisKeyBuilder redisKeyBuilder(RedisKeyProperties properties) {
        return new RedisKeyBuilder(properties);
    }

    @Bean
    @Conditional(RedisKeyLoadMatch.class)
    public MsgProviderRedisKeyBuilder msgProviderRedisBuilder(RedisKeyProperties properties) {
        return new MsgProviderRedisKeyBuilder(properties);
    }

    @Bean
    @Conditional(RedisKeyLoadMatch.class)
    public ImProviderRedisKeyBuilder imProviderRedisKeyBuilder(RedisKeyProperties properties) {
        return new ImProviderRedisKeyBuilder(properties);
    }

    @Bean
    @Conditional(RedisKeyLoadMatch.class)
    public ImCoreServerRedisKeyBuilder imCoreServerRedisKeyBuilder(RedisKeyProperties properties) {
        return new ImCoreServerRedisKeyBuilder(properties);
    }

    @Bean
    @Conditional(RedisKeyLoadMatch.class)
    public MsgProviderCommonRedisKeyBuilder msgProviderCommonRedisKeyBuilder(RedisKeyProperties properties) {
        return new MsgProviderCommonRedisKeyBuilder(properties);
    }
    
    @Bean
    @Conditional(RedisKeyLoadMatch.class)
    public ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder(RedisKeyProperties properties) {
        return new ImCoreServerCommonRedisKeyBuilder(properties);
    }

    @Bean
    @Conditional(RedisKeyLoadMatch.class)
    public LivingRoomRedisKeyBuilder livingRoomRedisKeyBuilder(RedisKeyProperties properties) {
        return new LivingRoomRedisKeyBuilder(properties);
    }

    @Bean
    @Conditional(RedisKeyLoadMatch.class)
    public GiftProviderRedisKeyBuilder giftProviderRedisKeyBuilder(RedisKeyProperties properties) {
        return new GiftProviderRedisKeyBuilder(properties);
    }
}
