package com.aiolos.live.gift.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.gift.interfaces.dto.GIftRecordDTO;
import com.aiolos.live.gift.provider.service.GiftRecordService;
import com.aiolos.live.model.po.LivingGiftRecord;
import com.aiolos.live.service.LivingGiftRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class GiftRecordServiceImpl implements GiftRecordService {

    @Resource
    private LivingGiftRecordService livingGiftRecordService;
    
    @Override
    public void insert(GIftRecordDTO dto) {
        if (dto == null || dto.getGiftId() == null || dto.getStreamerId() == null || dto.getUserId() == null) return;
        dto.setId( null);
        livingGiftRecordService.save(ConvertBeanUtil.convert(dto, LivingGiftRecord.class));
    }
}
