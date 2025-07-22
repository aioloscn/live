package com.aiolos.live.mq.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImAckDelayMessage implements Serializable {
    
    private String bodyJson;
}
