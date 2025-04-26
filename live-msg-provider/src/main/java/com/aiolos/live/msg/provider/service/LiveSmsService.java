package com.aiolos.live.msg.provider.service;

import com.aiolos.live.enums.MsgSendResultEnum;

public interface LiveSmsService {
    
    MsgSendResultEnum sendSms(String phone);
}
