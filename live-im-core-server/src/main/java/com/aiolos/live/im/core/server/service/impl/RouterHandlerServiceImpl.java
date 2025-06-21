package com.aiolos.live.im.core.server.service.impl;

import com.aiolos.live.im.core.server.common.ChannelHandlerContextCache;
import com.aiolos.live.im.core.server.common.ImMsg;
import com.aiolos.live.im.core.server.service.MsgAckService;
import com.aiolos.live.im.core.server.service.RouterHandlerService;
import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RouterHandlerServiceImpl implements RouterHandlerService {

    @Resource
    private MsgAckService msgAckService;
    
    @Override
    public void receiveMsg(ImMsgBody imMsgBody) {
        if (sendMsgToClient(imMsgBody)) {
            // 当im服务器推送了消息给客户端，记录下ack map，当客户端ack后删除
            msgAckService.recordMsgAck(imMsgBody, 1);
            msgAckService.sendDelayMsg(imMsgBody);
        }
    }

    @Override
    public boolean sendMsgToClient(ImMsgBody imMsgBody) {
        // 需要进行消息通知的userId，也就是发送者消息包里的receiverId
        Long userId = imMsgBody.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        if (ctx != null) {
            if (StringUtils.isBlank(imMsgBody.getMsgId())) {
                imMsgBody.setMsgId(UUID.randomUUID().toString());
            }
            // 回写给客户端
            ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBody)));
            return true;
        }
        return false;
    }
}
