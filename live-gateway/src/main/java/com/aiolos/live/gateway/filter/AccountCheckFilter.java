package com.aiolos.live.gateway.filter;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.common.enums.GatewayHeaderEnum;
import com.aiolos.live.account.dto.AccountDTO;
import com.aiolos.live.account.interfaces.AccountTokenRpc;
import com.aiolos.live.common.keys.GatewayApplicationProperties;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class AccountCheckFilter implements GlobalFilter, Ordered {

    @DubboReference
    private AccountTokenRpc accountTokenRpc;
    
    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> whitelistUrls = gatewayApplicationProperties.getUrls();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (path.contains("api-docs")) {
            return chain.filter(exchange);
        }
        if (StringUtils.isBlank(path)) {
            return Mono.empty();
        }

        for (String whitelistUrl : whitelistUrls) {
            if (path.startsWith(whitelistUrl)) {
                // 不需要token校验，放行到下游服务
                return chain.filter(exchange);
            }
        }

        // 不在白名单的请求需要提取cookie做校验
        String token = resolveToken(exchange);
        AccountDTO accountDto = null;

        if (StringUtils.isNotBlank(token)) {
            // 根据token获取userId
            accountDto = accountTokenRpc.getUserByToken(token);
        }

        ServerHttpRequest.Builder builder = request.mutate();
        
        if (accountDto != null && accountDto.getUserId() != null) {

            // 将用户信息放入请求头，下游服务可以从请求头中获取，再放入ContextInfo中
            builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getHeaderName(), accountDto.getUserId().toString());

            String userJson = JSON.toJSONString(accountDto);
            String encodedJson = Base64.getEncoder().encodeToString(userJson.getBytes(StandardCharsets.UTF_8));
            builder.header(GatewayHeaderEnum.USER_INFO_JSON.getHeaderName(), encodedJson);
            builder.header(GatewayHeaderEnum.IS_ANONYMOUS.getHeaderName(), "false");
        } else {
            // 匿名用户处理
            String deviceId = resolveDeviceId(exchange);
            Long anonymousId = accountTokenRpc.getOrCreateAnonymousId(deviceId);
            builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getHeaderName(), anonymousId.toString());
            builder.header(GatewayHeaderEnum.DEVICE_ID.getHeaderName(), deviceId);
            builder.header(GatewayHeaderEnum.IS_ANONYMOUS.getHeaderName(), "true");
        }
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    private String resolveToken(ServerWebExchange exchange) {
        List<HttpCookie> tokens = exchange.getRequest().getCookies().get("live-token");
        if (CollectionUtil.isNotEmpty(tokens) && StringUtils.isNotBlank(tokens.get(0).getValue())) {
            return tokens.get(0).getValue();
        }
        return null;
    }

    private String resolveDeviceId(ServerWebExchange exchange) {
        String deviceId = null;
        ServerHttpRequest request = exchange.getRequest();
        List<String> deviceHeaders = request.getHeaders().get("X-Device-ID");
        if (CollectionUtil.isNotEmpty(deviceHeaders)) {
            deviceId = deviceHeaders.get(0);
        }

        if (StringUtils.isBlank(deviceId)) {
            HttpCookie deviceCookie = request.getCookies().getFirst("device-id");
            if (deviceCookie != null) {
                deviceId = deviceCookie.getValue();
            }
        }

        if (StringUtils.isBlank(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            ResponseCookie deviceCookie = ResponseCookie.from("device-id", deviceId)
                    .maxAge(Duration.ofDays(7))
                    .httpOnly(true)
//                    .secure(true) // 仅https传输
                    .domain("live.aiolos.com")
                    .path("/")
                    .build();
            exchange.getResponse().getHeaders().set("Access-Control-Allow-Credentials", "true");
            exchange.getResponse().addCookie(deviceCookie);
        }
        return deviceId;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
