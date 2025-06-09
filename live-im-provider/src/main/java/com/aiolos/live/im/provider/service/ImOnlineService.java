package com.aiolos.live.im.provider.service;

public interface ImOnlineService {

    /**
     * 判断用户是否在线
     * @param appId
     * @param userId
     * @return
     */
    boolean isOnline(Integer appId, Long userId);
}
