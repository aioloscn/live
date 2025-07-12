package com.aiolos.live.gift.api;

import com.aiolos.live.gift.api.dto.GiftConfigDTO;

import java.util.List;

public interface GiftConfigApi {

    GiftConfigDTO getGiftById(Long id);

    List<GiftConfigDTO> queryGiftList();
    
    void insertGift(GiftConfigDTO dto);
    
    void updateGift(GiftConfigDTO dto);
}
