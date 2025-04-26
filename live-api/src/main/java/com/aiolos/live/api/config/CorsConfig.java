package com.aiolos.live.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // sessionId多次访问一致
        corsConfiguration.setAllowCredentials(true);
        // 允许访问的客户端域名
        List<String> allowedOriginsPatterns = new ArrayList<>();
        allowedOriginsPatterns.add("*");
        corsConfiguration.setAllowedOriginPatterns(allowedOriginsPatterns);
        // 允许任何header
        corsConfiguration.addAllowedHeader("*");
        // 允许任何method
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }
}
