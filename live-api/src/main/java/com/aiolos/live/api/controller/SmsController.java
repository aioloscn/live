package com.aiolos.live.api.controller;

import com.aiolos.live.enums.MsgSendResultEnum;
import com.aiolos.live.msg.interfaces.SmsRpc;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@Tag(name = "短信服务")
public class SmsController {

    @DubboReference
    private SmsRpc smsRpc;

    @PostMapping("/send-sms")
    MsgSendResultEnum sendSms(String phone) {
        try {
            return smsRpc.sendSms(phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MsgSendResultEnum.FAIL;
    }
}
