package com.aiolos.live.msg.interfaces;

import com.aiolos.common.model.response.CommonResponse;

public interface SmsRpc {

    /**
     * 发送短信
     * @param phone
     * @return
     */
    CommonResponse sendSms(String phone);
}
