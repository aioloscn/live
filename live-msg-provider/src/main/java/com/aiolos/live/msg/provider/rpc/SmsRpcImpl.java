package com.aiolos.live.msg.provider.rpc;

import com.aiolos.live.enums.MsgSendResultEnum;
import com.aiolos.live.msg.interfaces.SmsRpc;
import com.aiolos.live.msg.provider.service.LiveSmsService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class SmsRpcImpl implements SmsRpc {

    @Resource
    private LiveSmsService liveSmsService;
    
    @Override
    public MsgSendResultEnum sendSms(String phone) {
        return liveSmsService.sendSms(phone);
    }
}
