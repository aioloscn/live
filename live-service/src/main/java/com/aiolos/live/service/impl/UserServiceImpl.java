package com.aiolos.live.service.impl;

import com.aiolos.live.mapper.UserMapper;
import com.aiolos.live.model.po.User;
import com.aiolos.live.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    
}
