package com.aiolos.live.account.provider.service;

import com.aiolos.live.account.dto.AccountDTO;

public interface AccountTokenService {

    String createToken(Long userId);

    AccountDTO getUserByToken(String token);

    Long getOrCreateAnonymousId(String deviceId);
}
