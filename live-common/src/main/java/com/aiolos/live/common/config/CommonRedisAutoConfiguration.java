package com.aiolos.live.common.config;

import com.aiolos.live.common.keys.*;
import com.aiolos.live.common.keys.builder.*;
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
    public UserProviderRedisKeyBuilder userRedisKeyBuilder(RedisKeyProperties properties) {
        return new UserProviderRedisKeyBuilder(properties);
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
}
