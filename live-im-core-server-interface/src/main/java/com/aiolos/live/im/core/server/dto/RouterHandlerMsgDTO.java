package com.aiolos.live.im.core.server.dto;

import com.aiolos.live.im.interfaces.dto.ImMsgBody;
import lombok.Data;

import java.io.Serializable;

@Data
public class RouterHandlerMsgDTO implements Serializable {
    
    private ImMsgBody imMsgBody;
}
