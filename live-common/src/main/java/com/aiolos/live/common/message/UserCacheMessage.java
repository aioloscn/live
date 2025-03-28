package com.aiolos.live.common.message;

import com.aiolos.live.enums.UserCacheEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserCacheMessage implements Serializable {
    
    private Long userId;
    private UserCacheEnum userCacheEnum;
}
