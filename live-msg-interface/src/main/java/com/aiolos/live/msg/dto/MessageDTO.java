package com.aiolos.live.msg.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageDTO implements Serializable {
    
    private Long roomId;
    
    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 消息内容
     */
    private String content;
    
    private String senderName;
    private String senderAvatar;
    
    private Date createTime;
    private Date updateTime;
}
