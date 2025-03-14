package com.aiolos.live.common.config;

import com.aiolos.live.common.keys.RedisKeyBuilder;
import com.aiolos.live.common.keys.RedisKeyProperties;
import com.aiolos.live.common.keys.UserRedisKeyBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisKeyProperties.class)
public class CommonRedisAutoConfiguration {

    @Bean
    public RedisKeyBuilder redisKeyBuilder(RedisKeyProperties properties) {
        return new RedisKeyBuilder(properties);
    }

    @Bean
    public UserRedisKeyBuilder userRedisKeyBuilder(RedisKeyProperties properties) {
        return new UserRedisKeyBuilder(properties);
    }
}
