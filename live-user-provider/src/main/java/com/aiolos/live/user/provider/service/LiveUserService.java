package com.aiolos.live.user.provider.service;

import com.aiolos.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface LiveUserService {

    UserDTO getUserById(Long userId);

    void insertUser(UserDTO userDTO);

    void updateUserInfo(UserDTO userDTO);

    Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIds);
}
