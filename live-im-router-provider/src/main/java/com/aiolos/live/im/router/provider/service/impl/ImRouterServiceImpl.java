package com.aiolos.live.im.router.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.im.core.server.dto.RouterHandlerMsgDTO;
import com.aiolos.live.im.core.server.interfaces.RouterHandlerRpc;
import com.aiolos.live.im.router.dto.RouterMsgDTO;
import com.aiolos.live.im.router.provider.service.ImRouterService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private RouterHandlerRpc routerHandlerRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder;
    
    @Override
    public void sendMsg(RouterMsgDTO dto) {
        String serverIpAddress = stringRedisTemplate.opsForValue().get(imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(dto.getImMsgBody().getAppId(), dto.getImMsgBody().getUserId()));
        if (StringUtils.isBlank(serverIpAddress)) {
            // 用户不在线
            return;
        }
        // host:port
        RpcContext.getContext().set("ip", serverIpAddress);
        routerHandlerRpc.sendMsg(ConvertBeanUtil.convert(dto, RouterHandlerMsgDTO::new)); 
    }
}
