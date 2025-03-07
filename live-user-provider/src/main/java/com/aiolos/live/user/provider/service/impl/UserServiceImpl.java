package com.aiolos.live.user.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtils;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.po.User;
import com.aiolos.live.user.provider.mapper.UserMapper;
import com.aiolos.live.user.provider.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Aiolos
 * @since 2025-03-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
    }
}
