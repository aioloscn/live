package com.aiolos.live.dubbo.common.config;

import com.aiolos.live.dubbo.common.filter.CustomExceptionFilter;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.rpc.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboBeanConfig {

    /**
     * 全局 Provider 配置
     */
    @Bean
    public ProviderConfig providerConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        // 移除默认的 exception 过滤器，添加自定义过滤器
        // 显示指定了过滤器链，CustomExceptionFilter就没必要加@Activate(group = "provider")了，会失效
        // /resources/META-INF/dubbo/org.apache.dubbo.rpc.Filter也不需要了
        providerConfig.setFilter("-exception,customExceptionFilter");
        return providerConfig;
    }

    /**
     * 注册自定义dubbo异常过滤器
     */
    @Bean
    public Filter customExceptionFilter() {
        return new CustomExceptionFilter();
    }
}
