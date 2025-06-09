package com.aiolos.live.im.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class ImMsgBody implements Serializable {
    
    @Schema(description = "接入im服务的各个业务线id")
    private int appId;
    @Schema(description = "用户id")
    private long userId;
    @Schema(description = "从业务服务中获取，用于在im服务建立连接时使用")
    private String token;
    @Schema(description = "业务标识")
    private int bizCode;
    @Schema(description = "消息id，用于ack")
    private String msgId;
    @Schema(description = "和业务服务进行消息传递")
    private String data;
}
