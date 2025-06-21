package com.aiolos.live.common.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImOnlineMessage implements Serializable {
    
    private Long userId;
    private Integer appId;
    private Long roomId;
    private Long loginTime;
}
