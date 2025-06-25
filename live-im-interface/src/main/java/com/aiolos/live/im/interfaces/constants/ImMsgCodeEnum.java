package com.aiolos.live.im.interfaces.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ImMsgCodeEnum {

    IM_LOGIN_MSG(1001, "登录消息包"),
    IM_LOGOUT_MSG(1002, "登出消息包"),
    IM_BIZ_MSG(1003, "业务消息包"),
    IM_HEARTBEAT_MSG(1004, "心跳消息包"),
    IM_ACK_MSG(1005, "ack消息包"),
    ;
    
    private int code;
    private String desc;
    
    public static ImMsgCodeEnum getEnumByCode(int code) {
        Optional<ImMsgCodeEnum> optional = Arrays.asList(values()).stream().filter(v -> v.getCode() == code).findAny();
        return optional.orElse(null);
    }
}
