package com.aiolos.live.im.core.server.service.impl;

import com.aiolos.live.common.keys.builder.ImCoreServerRedisKeyBuilder;
import com.aiolos.live.im.core.server.mq.producer.ImMsgProducer;
import com.aiolos.live.im.core.server.service.MsgAckService;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MsgAckServiceImpl implements MsgAckService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerRedisKeyBuilder imCoreServerRedisKeyBuilder;
    @Autowired
    private ImMsgProducer imMsgProducer;
    
    @Override
    public void doMsgAck(ImMsgBody imMsgBody) {
        redisTemplate.opsForHash().delete(imCoreServerRedisKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId()), imMsgBody.getMsgId());
    }

    @Override
    public void recordMsgAck(ImMsgBody imMsgBody, int times) {
        redisTemplate.opsForHash().put(imCoreServerRedisKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId()), imMsgBody.getMsgId(), times);
    }

    @Override
    public void sendDelayMsg(ImMsgBody imMsgBody) {
        imMsgProducer.sendAckDelayMsg(imMsgBody);
    }

    @Override
    public int getMsgAckTimes(String msgId, Long userId, Integer appId) {
        Object times = redisTemplate.opsForHash().get(imCoreServerRedisKeyBuilder.buildImAckMapKey(userId, appId), msgId);
        if (times ==  null) return -1;
        return (int) times;
    }
}
