package com.aiolos.live.user.interfaces;

import com.aiolos.live.user.dto.UserDTO;

public interface IUserRpc {

    UserDTO getUserById(Long userId);
}
