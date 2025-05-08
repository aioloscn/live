package com.aiolos.live.common.config;

import com.aiolos.live.common.keys.*;
import com.aiolos.live.common.keys.builder.MsgProviderRedisBuilder;
import com.aiolos.live.common.keys.builder.RedisKeyBuilder;
import com.aiolos.live.common.keys.builder.UserProviderRedisKeyBuilder;
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
    public MsgProviderRedisBuilder msgProviderRedisBuilder(RedisKeyProperties properties) {
        return new MsgProviderRedisBuilder(properties);
    }
}
