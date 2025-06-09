package com.aiolos.live.im.interfaces.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizCodeEnum {

    LIVING_CHAT_MSG(2222, "直播间聊天消息"),
    ;
    
    private int code;
    private String desc;
}
