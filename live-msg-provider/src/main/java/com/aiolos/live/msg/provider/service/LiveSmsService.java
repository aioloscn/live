package com.aiolos.live.msg.provider.service;

import com.aiolos.common.model.response.CommonResponse;

public interface LiveSmsService {
    
    CommonResponse sendSms(String phone);
}
