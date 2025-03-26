package com.aiolos.live.id.generator.provider.service;

public interface LiveIdGeneratorService {

    Long getSeqId(Integer id);

    Long getNonSeqId(Integer id);
}
