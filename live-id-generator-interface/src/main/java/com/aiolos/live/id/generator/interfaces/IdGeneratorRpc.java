package com.aiolos.live.id.generator.interfaces;

public interface IdGeneratorRpc {

    Long getSeqId(Integer id);

    Long getNonSeqId(Integer id);
}
