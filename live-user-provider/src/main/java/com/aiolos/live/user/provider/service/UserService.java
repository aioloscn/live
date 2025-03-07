package com.aiolos.live.user.provider.service;

import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Aiolos
 * @since 2025-03-04
 */
public interface UserService extends IService<User> {

    UserDTO getUserById(Long userId);
}
