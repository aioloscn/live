package com.aiolos.live.msg.provider.mq.consumer;

import com.aiolos.live.common.message.ImBizMessage;
import com.aiolos.live.im.interfaces.constants.AppIdEnum;
import com.aiolos.live.im.interfaces.constants.BizCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.router.dto.RouterMsgDTO;
import com.aiolos.live.im.router.interfaces.ImRouterRpc;
import com.aiolos.live.msg.dto.MessageDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class ImMsgConsumer {
    
    @DubboReference
    private ImRouterRpc imRouterRpc;
    
    @Bean
    public Consumer<ImBizMessage> imBizMsg() {
        return message -> {
            ImMsgBody imMsgBody = JSON.parseObject(new String(message.getBody()), ImMsgBody.class);
            log.info("received msg: {}", new String(message.getBody()));
            if (imMsgBody.getBizCode() == BizCodeEnum.LIVING_CHAT_MSG.getCode()) {
                MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
                ImMsgBody body = new ImMsgBody();
                body.setUserId(messageDTO.getReceiverId());
                body.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
                body.setBizCode(BizCodeEnum.LIVING_CHAT_MSG.getCode());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("senderId", messageDTO.getUserId());
                jsonObject.put("content", messageDTO.getContent());
                body.setData(JSON.toJSONString(jsonObject));
                RouterMsgDTO dto = new RouterMsgDTO();
                dto.setImMsgBody(body);
                imRouterRpc.sendMsg(dto);
            }
        };
    }
}
