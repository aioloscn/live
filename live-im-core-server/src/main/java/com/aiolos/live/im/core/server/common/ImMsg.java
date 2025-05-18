package com.aiolos.live.im.core.server.common;

import com.aiolos.live.im.interfaces.constants.ImConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImMsg implements Serializable {
    
    // 魔数 用于做基本校验
    private short magic;
    
    // 标识当前消息的作用，交给不同的handler去处理
    private int code;

    // 消息体的长度
    private int len;
    
    // 消息体
    private byte[] body;

    public static ImMsg build(int code, String data) {
        ImMsg msg = new ImMsg();
        msg.setMagic(ImConstants.DEFAULT_MAGIC);
        msg.setCode(code);
        msg.setBody(data.getBytes());
        msg.setLen(msg.getBody().length);
        return msg;
    }
}
