package com.aiolos.live.user.provider.service;

import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.model.bo.LoginBO;
import com.aiolos.live.user.provider.model.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

public interface LiveUserService {

    String createToken(Long userId);

    Long getUserIdByToken(String token);

    UserVO login(LoginBO loginBO, HttpServletResponse response);

    UserVO getUserById(Long userId);

    void insertUser(UserDTO userDTO);

    void updateUserInfo(UserDTO userDTO);

    Map<Long, UserVO> batchQueryUserInfo(List<Long> userIds);

    UserVO queryByPhone(String phone);
}
