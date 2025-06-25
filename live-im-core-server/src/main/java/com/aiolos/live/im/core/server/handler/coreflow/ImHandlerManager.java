package com.aiolos.live.im.core.server.handler.coreflow;

import com.aiolos.live.im.interfaces.constants.ImMsgCodeEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ImHandlerManager {

    private Map<ImMsgCodeEnum, AbstractImHandlerProcessor> processorMap = new HashMap<>();
    
    public void register(ImMsgCodeEnum code, AbstractImHandlerProcessor processor) {
        processorMap.put(code, processor);
    }
    
    public AbstractImHandlerProcessor getProcessor(ImMsgCodeEnum code) {
        return processorMap.get(code);
    }
}
