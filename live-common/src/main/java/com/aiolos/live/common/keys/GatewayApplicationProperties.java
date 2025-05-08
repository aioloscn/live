package com.aiolos.live.common.keys;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "whitelist.live")
public class GatewayApplicationProperties {
    
    // live项目不需要校验token的域名
    private List<String> urls;
}
