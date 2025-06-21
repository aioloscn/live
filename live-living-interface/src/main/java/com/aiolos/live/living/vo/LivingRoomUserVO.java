package com.aiolos.live.living.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LivingRoomUserVO implements Serializable {
    
    private List<Long> userIds;
}
