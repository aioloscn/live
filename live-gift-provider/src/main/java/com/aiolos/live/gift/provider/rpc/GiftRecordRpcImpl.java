package com.aiolos.live.gift.provider.rpc;

import com.aiolos.live.gift.interfaces.GiftRecordRpc;
import com.aiolos.live.gift.interfaces.dto.GIftRecordDTO;
import com.aiolos.live.gift.provider.service.GiftRecordService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class GiftRecordRpcImpl implements GiftRecordRpc {

    @Resource
    private GiftRecordService giftRecordService;
    
    @Override
    public void insert(GIftRecordDTO dto) {
        giftRecordService.insert(dto);
    }
}
