package com.aiolos.live.common.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImOfflineMessage implements Serializable {
    
    private Long userId;
    private Integer appId;
    private Long roomId;
    private Long logoutTime;
}
