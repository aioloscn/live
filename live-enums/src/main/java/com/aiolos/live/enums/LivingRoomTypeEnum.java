package com.aiolos.live.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LivingRoomTypeEnum {

    DEFAULT_LIVING_ROOM(1, "常规直播间"),
    PK_LIVING_ROOM(2, "PK房"),
    ;
    
    private Integer code;
    private String desc;
}
