package com.aiolos.live.im.router.dto;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import lombok.Data;

import java.io.Serializable;

@Data
public class RouterMsgDTO implements Serializable {
    
    private ImMsgBody imMsgBody;
}
