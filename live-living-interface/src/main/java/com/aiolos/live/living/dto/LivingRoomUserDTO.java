package com.aiolos.live.living.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LivingRoomUserDTO implements Serializable {
    
    private Long roomId;
    private Integer appId;
}
