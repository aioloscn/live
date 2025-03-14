package com.aiolos.live.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKeyConstant {

    USER_INFO_KEY("userInfo:", 60 * 60 * 24 * 7),
    ;

    private final String key;
    private final Integer expire;
}
