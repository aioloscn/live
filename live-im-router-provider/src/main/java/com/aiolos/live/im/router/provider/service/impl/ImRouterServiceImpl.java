package com.aiolos.live.im.router.provider.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.im.core.server.interfaces.RouterHandlerRpc;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.router.provider.service.ImRouterService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private RouterHandlerRpc routerHandlerRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder;
    
    @Override
    public void sendMsg(ImMsgBody imMsgBody) {
        String serverIpAddress = stringRedisTemplate.opsForValue().get(imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(imMsgBody.getAppId(), imMsgBody.getUserId()));
        if (StringUtils.isBlank(serverIpAddress)) {
            // 用户不在线
            return;
        }
        // host:port
        RpcContext.getContext().set("ip", serverIpAddress.substring(0, serverIpAddress.indexOf("%")));
        routerHandlerRpc.sendMsg(imMsgBody); 
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodies) {
        Map<Long, List<ImMsgBody>> userMsgBodyMap = imMsgBodies.stream().collect(Collectors.groupingBy(ImMsgBody::getUserId));
        List<String> serverIpAddressList = imMsgBodies.stream().map(o -> imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(o.getAppId(), o.getUserId())).collect(Collectors.toList());
        List<String> ipUserList = stringRedisTemplate.opsForValue().multiGet(serverIpAddressList);
        Map<String, List<Long>> ipUserMap = new HashMap<>();
        
        if (CollectionUtil.isNotEmpty(ipUserList)) {
            for (String ipUser : ipUserList) {
                
                if (StringUtils.isBlank(ipUser) || !ipUser.contains("%"))
                    continue;
                String[] parts = ipUser.split("%", 2);
                String ip = parts[0];
                Long userId = Long.parseLong(parts[1]);
                ipUserMap.computeIfAbsent(ip, k -> new ArrayList<>()).add(userId);
            }
            
            // 向同一台服务器的用户批量发送消息
            ipUserMap.forEach((ip, userIds) -> {
                List<ImMsgBody> msgBodyList = new ArrayList<>();
                userIds.forEach(userId -> msgBodyList.addAll(userMsgBodyMap.get(userId)));
                RpcContext.getContext().set("ip", ip);
                routerHandlerRpc.batchSendMsg(msgBodyList);
            });
        }
    }
}
