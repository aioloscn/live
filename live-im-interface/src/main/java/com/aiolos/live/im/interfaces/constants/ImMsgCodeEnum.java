package com.aiolos.live.im.interfaces.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImMsgCodeEnum {

    IM_LOGIN_MSG(1001, "登录消息包"),
    IM_LOGOUT_MSG(1002, "登出消息包"),
    IM_BIZ_MSG(1003, "业务消息包"),
    IM_HEARTBEAT_MSG(1004, "心跳消息包"),
    ;
    
    private Integer code;
    private String desc;
}
