package com.aiolos.live.im.interfaces.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppIdEnum {
    
    LIVE_APP_ID(10001, "直播业务"),
    ;
    
    private Integer code;
    private String desc;
}
