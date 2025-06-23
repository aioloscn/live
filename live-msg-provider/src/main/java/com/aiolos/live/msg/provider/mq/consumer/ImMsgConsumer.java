package com.aiolos.live.msg.provider.mq.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.live.common.message.ImBizMessage;
import com.aiolos.live.im.interfaces.constants.AppIdEnum;
import com.aiolos.live.im.interfaces.constants.BizCodeEnum;
import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import com.aiolos.live.im.router.interfaces.ImRouterRpc;
import com.aiolos.live.living.dto.LivingRoomUserDTO;
import com.aiolos.live.living.interfaces.LivingRoomRpc;
import com.aiolos.live.living.vo.LivingRoomUserVO;
import com.aiolos.live.msg.dto.MessageDTO;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class ImMsgConsumer {
    
    @DubboReference
    private ImRouterRpc imRouterRpc;
    @DubboReference
    private LivingRoomRpc livingRoomRpc;
    
    @Bean
    public Consumer<ImBizMessage> imBizMsg() {
        return message -> {
            ImMsgBody imMsgBody = JSON.parseObject(new String(message.getBody()), ImMsgBody.class);
            log.info("received msg: {}", new String(message.getBody()));
            if (imMsgBody.getBizCode() == BizCodeEnum.LIVING_CHAT_MSG.getCode()) {
                
                MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
                LivingRoomUserDTO dto = new LivingRoomUserDTO();
                dto.setRoomId(messageDTO.getRoomId());
                dto.setAppId(imMsgBody.getAppId());
                LivingRoomUserVO vo = livingRoomRpc.queryLivingRoomUser(dto);
                if (vo != null && CollectionUtil.isNotEmpty(vo.getUserIds())) {

                    List<ImMsgBody> imMsgBodies = new ArrayList<>();
                    vo.getUserIds().forEach(userId -> {
                        
                        if (userId.equals(imMsgBody.getUserId())) return;
                        ImMsgBody respBody = new ImMsgBody();
                        respBody.setUserId(userId);
                        respBody.setAppId(AppIdEnum.LIVE_APP_ID.getCode());
                        respBody.setBizCode(BizCodeEnum.LIVING_CHAT_MSG.getCode());
                        respBody.setData(imMsgBody.getData());
                        imMsgBodies.add(respBody);
                    });
                    imRouterRpc.batchSendMsg(imMsgBodies);
                }
            }
        };
    }
}
