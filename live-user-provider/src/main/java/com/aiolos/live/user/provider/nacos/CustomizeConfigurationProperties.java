package com.aiolos.live.user.provider.nacos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "customize.config")
public class CustomizeConfigurationProperties {
    
    private String testRefresh;
}
