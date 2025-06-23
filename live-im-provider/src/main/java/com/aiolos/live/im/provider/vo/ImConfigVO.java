package com.aiolos.live.im.provider.vo;

import lombok.Data;

@Data
public class ImConfigVO {
    
    private String token;
    private String wsImServerAddress;
    private String tcpImServerAddress;
}
