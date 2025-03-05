package com.aiolos.live.user.provider.service.impl;

import com.aiolos.live.user.provider.po.User;
import com.aiolos.live.user.provider.mapper.UserMapper;
import com.aiolos.live.user.provider.service.UserService;
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
