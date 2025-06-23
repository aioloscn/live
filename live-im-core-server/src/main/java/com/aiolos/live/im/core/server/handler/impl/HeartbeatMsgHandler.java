package com.aiolos.live.im.core.server.handler.impl;

import com.aiolos.live.common.keys.builder.ImCoreServerRedisKeyBuilder;
import com.aiolos.live.common.keys.builder.common.ImCoreServerCommonRedisKeyBuilder;
import com.aiolos.live.im.core.server.common.ImContextUtil;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.handler.SimpleHandler;
import com.aiolos.live.im.interfaces.constants.ImConstants;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HeartbeatMsgHandler implements SimpleHandler {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerRedisKeyBuilder imCoreServerRedisKeyBuilder;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerCommonRedisKeyBuilder imCoreServerCommonRedisKeyBuilder;
    
    @Override
    public void handle(ChannelHandlerContext ctx, ImMsg msg) {
        // 心跳包基本检测
        Long userId = ImContextUtil.getUserId(ctx);
        Integer appId = ImContextUtil.getAppId(ctx);
        if (userId == null || appId == null) {
            ctx.close();
            throw new IllegalArgumentException("attr data missing");
        }
        
        // redis存储心跳记录
        String heartbeatZSetKey = imCoreServerRedisKeyBuilder.buildImOnlineZSetKey(userId, appId);
        this.recordOnlineTime(userId, heartbeatZSetKey);
        this.removeExpiredRecords(heartbeatZSetKey);
        // 该客户端5分钟内没有发送心跳包则清空所有心跳记录表示断开
        redisTemplate.expire(heartbeatZSetKey, 5, TimeUnit.MINUTES);
        // 延长用户绑定的所在服务器ip的缓存有效期
        stringRedisTemplate.expire(imCoreServerCommonRedisKeyBuilder.buildImBindIpKey(appId, userId), ImConstants.DEFAULT_HEARTBEAT_GAP * 5, TimeUnit.SECONDS);

        ImMsgBody respBody  = new ImMsgBody();
        respBody.setUserId(userId);
        respBody.setAppId(appId);
        respBody.setData("true");
        ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(respBody)));
    }

    private void recordOnlineTime(Long userId, String heartbeatZSetKey) {
        // 新增或更新
        redisTemplate.opsForZSet().add(heartbeatZSetKey, userId, System.currentTimeMillis());
    }

    /**
     * 清理过期的心跳记录，30s发送一次，删除60s前的记录
     * @param heartbeatZSetKey
     */
    private void removeExpiredRecords(String heartbeatZSetKey) {
        redisTemplate.opsForZSet().removeRangeByScore(heartbeatZSetKey, 0, System.currentTimeMillis() - ImConstants.DEFAULT_HEARTBEAT_GAP * 1000 * 2);
    }
}
