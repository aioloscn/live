package com.aiolos.live.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgSendResultEnum {
    
    SUCCESS(1, "发送成功"),
    FAIL(2, "发送失败"),
    PARAM_ERROR(3, "消息参数异常");
    
    private Integer code;
    private String desc;
}
