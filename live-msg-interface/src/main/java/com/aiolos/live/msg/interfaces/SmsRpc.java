package com.aiolos.live.msg.interfaces;

import com.aiolos.live.enums.MsgSendResultEnum;

public interface SmsRpc {

    /**
     * 发送短信
     * @param phone
     * @return
     */
    MsgSendResultEnum sendSms(String phone);
}
