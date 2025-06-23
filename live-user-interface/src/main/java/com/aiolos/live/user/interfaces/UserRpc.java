package com.aiolos.live.user.interfaces;

import com.aiolos.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface UserRpc {

    UserDTO getUserById(Long userId);

    void insertUser(UserDTO userDTO);

    void updateUserInfo(UserDTO userDTO);

    Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIds);
}
