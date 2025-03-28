package com.aiolos.live.user.provider.service;

import com.aiolos.live.enums.UserTagEnum;

public interface LiveUserTagService {

    boolean setTag(Long userId, UserTagEnum userTagEnum);
    
    boolean cancelTag(Long userId, UserTagEnum userTagEnum);
    
    boolean checkTag(Long userId, UserTagEnum userTagEnum);
}
