package com.aiolos.live.living.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StreamingDTO implements Serializable {
    
    private Long streamerId;
    private Long roomId;
    private String roomName;
    private Integer type;
    private String cover;
}
