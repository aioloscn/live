package com.aiolos.live.common.keys;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "whitelist.live")
public class GatewayApplicationProperties {

    /**
     * live项目不需要校验token的域名
     */
    private List<String> urls;

    /**
     * 允许匿名访问的url，会生成匿名userId
     */
    private List<String> anonymousUrls;
}
