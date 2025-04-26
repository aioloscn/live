package com.aiolos.live.id.generator.provider.service;

public interface LiveIdGeneratorService {

    /**
     * 获取有序id
     * @param id 配置表主键
     * @return
     */
    Long getSeqId(Integer id);

    /**
     * 获取无序id
     * @param id 配置表主键
     * @return
     */
    Long getNonSeqId(Integer id);
}
