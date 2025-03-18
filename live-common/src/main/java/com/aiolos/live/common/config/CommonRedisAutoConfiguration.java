package com.aiolos.live.common.config;

import com.aiolos.live.common.keys.RedisKeyBuilder;
import com.aiolos.live.common.keys.RedisKeyLoadMatch;
import com.aiolos.live.common.keys.RedisKeyProperties;
import com.aiolos.live.common.keys.UserProviderRedisKeyBuilder;
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
}
