package com.aiolos.live.gift.interfaces;

import com.aiolos.live.gift.interfaces.dto.GiftConfigDTO;

import java.util.List;

public interface GiftConfigRpc {

    GiftConfigDTO getGiftById(Long id);

    List<GiftConfigDTO> queryGiftList();
    
    void insertGift(GiftConfigDTO dto);
    
    void updateGift(GiftConfigDTO dto);
}
