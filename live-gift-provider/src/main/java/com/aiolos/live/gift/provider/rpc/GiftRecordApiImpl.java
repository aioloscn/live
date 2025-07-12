package com.aiolos.live.gift.provider.rpc;

import com.aiolos.live.gift.api.GiftRecordApi;
import com.aiolos.live.gift.api.dto.GIftRecordDTO;
import com.aiolos.live.gift.provider.service.GiftRecordService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class GiftRecordApiImpl implements GiftRecordApi {

    @Resource
    private GiftRecordService giftRecordService;
    
    @Override
    public void insert(GIftRecordDTO dto) {
        giftRecordService.insert(dto);
    }
}
