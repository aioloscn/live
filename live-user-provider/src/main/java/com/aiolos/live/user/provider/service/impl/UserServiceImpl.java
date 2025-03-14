package com.aiolos.live.user.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtils;
import com.aiolos.live.common.constants.RedisKeyConstant;
import com.aiolos.live.common.keys.UserRedisKeyBuilder;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.provider.po.User;
import com.aiolos.live.user.provider.mapper.UserMapper;
import com.aiolos.live.user.provider.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Resource
    private UserRedisKeyBuilder userRedisKeyBuilder;

    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(userRedisKeyBuilder.buildUserInfoKey(userId));
        if (obj != null) {
            return ConvertBeanUtils.convert(obj, UserDTO.class);
        }
        UserDTO userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if (userDTO != null) {
            redisTemplate.opsForValue().set(userRedisKeyBuilder.buildUserInfoKey(userId), userDTO, RedisKeyConstant.USER_INFO_KEY.getExpire(), TimeUnit.SECONDS);
        }
        return userDTO;
    }
}
