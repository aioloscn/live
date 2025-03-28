package com.aiolos.live.user.interfaces;

import com.aiolos.live.enums.UserTagEnum;

public interface UserTagRpc {

    boolean setTag(Long userId, UserTagEnum userTagEnum);

    boolean cancelTag(Long userId, UserTagEnum userTagEnum);

    boolean checkTag(Long userId, UserTagEnum userTagEnum);
}
