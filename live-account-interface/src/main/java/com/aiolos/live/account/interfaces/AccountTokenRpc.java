package com.aiolos.live.account.interfaces;

import com.aiolos.live.account.dto.AccountDTO;

public interface AccountTokenRpc {

    String createToken(Long userId);

    AccountDTO getUserByToken(String token);

    Long getOrCreateAnonymousId(String deviceId);
}
