package com.aiolos.live.id.generator.provider.rpc;

import com.aiolos.live.id.generator.interfaces.IdGeneratorRpc;
import com.aiolos.live.id.generator.provider.service.LiveIdGeneratorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

@Slf4j
@DubboService
public class IdGeneratorRpcImpl implements IdGeneratorRpc {

    @Resource
    private LiveIdGeneratorService liveIdGeneratorService;

    @Override
    public Long getSeqId(Integer id) {
        return liveIdGeneratorService.getSeqId(id);
    }

    @Override
    public Long getNonSeqId(Integer id) {
        return liveIdGeneratorService.getNonSeqId(id);
    }
}
