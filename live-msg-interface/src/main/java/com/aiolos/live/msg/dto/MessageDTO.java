package com.aiolos.live.msg.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageDTO implements Serializable {
    
    private Long userId;
    
    private Long receiverId;
    
    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 消息内容
     */
    private String content;
    
    private Date createTime;
    private Date updateTime;
}
