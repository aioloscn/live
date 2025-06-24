package com.aiolos.live.account.provider.rpc;

import com.aiolos.live.account.dto.AccountDTO;
import com.aiolos.live.account.interfaces.AccountTokenRpc;
import com.aiolos.live.account.provider.service.AccountTokenService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class AccountTokenRpcImpl implements AccountTokenRpc {
    
    @Resource
    private AccountTokenService accountTokenService;

    @Override
    public String createToken(Long userId) {
        return accountTokenService.createToken(userId);
    }

    @Override
    public AccountDTO getUserByToken(String token) {
        return accountTokenService.getUserByToken(token);
    }

    @Override
    public Long getOrCreateAnonymousId(String deviceId) {
        return accountTokenService.getOrCreateAnonymousId(deviceId);
    }
}
