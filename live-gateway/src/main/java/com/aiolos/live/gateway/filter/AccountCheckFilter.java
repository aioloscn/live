package com.aiolos.live.gateway.filter;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.common.enums.GatewayHeaderEnum;
import com.aiolos.live.common.keys.GatewayApplicationProperties;
import com.aiolos.live.user.interfaces.UserRpc;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AccountCheckFilter implements GlobalFilter, Ordered {

    @DubboReference
    private UserRpc userRpc;
    
    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> whitelistUrls = gatewayApplicationProperties.getUrls();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (StringUtils.isBlank(path) || CollectionUtil.isEmpty(whitelistUrls))
            return Mono.empty();

        for (String whitelistUrl : whitelistUrls) {
            if (path.startsWith(whitelistUrl)) {
                // 不需要token校验，放行到下游服务
                return chain.filter(exchange);
            }
        }
        // 不在白名单的请求需要提取cookie做校验
        List<HttpCookie> tokens = request.getCookies().get("live-token");
        if (CollectionUtil.isEmpty(tokens))
            return Mono.empty();
        String token = tokens.get(0).getValue();
        if (StringUtils.isBlank(token))
            return Mono.empty();
        // 根据token获取userId
        Long userId = userRpc.getUserIdByToken(token);
        if (userId == null)
            return Mono.empty();
        // 将userId放入请求头，下游服务可以从请求头中获取
        ServerHttpRequest.Builder builder = request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getHeaderName(), String.valueOf(userId));
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
