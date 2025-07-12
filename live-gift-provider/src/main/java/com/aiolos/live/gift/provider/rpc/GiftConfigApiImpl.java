package com.aiolos.live.gift.provider.rpc;

import com.aiolos.live.gift.api.GiftConfigApi;
import com.aiolos.live.gift.api.dto.GiftConfigDTO;
import com.aiolos.live.gift.provider.service.GiftConfigService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
public class GiftConfigApiImpl implements GiftConfigApi {

    @Resource
    private GiftConfigService giftConfigService;
    
    @Override
    public GiftConfigDTO getGiftById(Long id) {
        return giftConfigService.getGiftById(id);
    }

    @Override
    public List<GiftConfigDTO> queryGiftList() {
        return giftConfigService.queryGiftList();
    }

    @Override
    public void insertGift(GiftConfigDTO dto) {
        giftConfigService.insertGift(dto);
    }

    @Override
    public void updateGift(GiftConfigDTO dto) {
        giftConfigService.updateGift(dto);
    }
}
