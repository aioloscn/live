package com.aiolos.live.im.interfaces;

public interface ImOnlineRpc {

    /**
     * 判断用户是否在线
     * @param appId
     * @param userId
     * @return
     */
    boolean isOnline(Integer appId, Long userId);
}
