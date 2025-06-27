package com.aiolos.live.gift.provider.service;

import com.aiolos.live.gift.interfaces.dto.GiftConfigDTO;

import java.util.List;

public interface GiftConfigService {

    GiftConfigDTO getGiftById(Long id);

    List<GiftConfigDTO> queryGiftList();

    void insertGift(GiftConfigDTO dto);

    void updateGift(GiftConfigDTO dto);
}
