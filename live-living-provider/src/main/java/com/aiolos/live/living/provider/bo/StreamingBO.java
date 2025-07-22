package com.aiolos.live.living.provider.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StreamingBO implements Serializable {
    
    private Long streamerId;
    private Long roomId;
    private String roomName;
    private Integer type;
    private String cover;
}
