package com.aiolos.live.common.keys;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.application")
public class RedisKeyProperties {

    private String name;
}
