package com.aiolos.live.mq.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImBizMessage implements Serializable {
    
    private byte[] body;
}
