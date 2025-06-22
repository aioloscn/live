package com.aiolos.live.api.service.impl;

import com.aiolos.common.model.ContextInfo;
import com.aiolos.live.api.service.ImService;
import com.aiolos.live.api.vo.ImConfigVO;
import com.aiolos.live.im.interfaces.constants.AppIdEnum;
import com.aiolos.live.im.interfaces.interfaces.ImTokenRpc;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ImServiceImpl implements ImService {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private DiscoveryClient discoveryClient;
    
    @Value("${im.ws.port}")
    private Integer wsPort;
    @Value("${im.tcp.port}")
    private Integer tcpPort;
    @Value("${im.service-instance}")
    private String serviceInstance;
    
    @Override
    public ImConfigVO getImConfig() {
        Long userId = ContextInfo.getUserId();
        if (userId == null) return null;
        String token = imTokenRpc.createImLoginToken(userId, AppIdEnum.LIVE_APP_ID.getCode());
        ImConfigVO vo = new ImConfigVO();
        vo.setToken(token);
        buildImServerAddress(vo);
        return vo;
    }

    private void buildImServerAddress(ImConfigVO vo) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceInstance);
        if (instances.isEmpty()) {
            throw new RuntimeException("No IM core service instance available!");
        }
        Collections.shuffle(instances); // 简单的随机负载均衡
        ServiceInstance instance = instances.get(0);
        vo.setWsImServerAddress(instance.getHost() + ":" + wsPort); // 前端去拼接 ws:// 地址
        vo.setTcpImServerAddress(instance.getHost() + ":" + tcpPort);
    }
}
