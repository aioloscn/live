package com.aiolos.live.user.provider.rpc;

import com.aiolos.common.utils.ConvertBeanUtils;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.interfaces.IUserRpc;
import com.aiolos.live.user.provider.po.User;
import com.aiolos.live.user.provider.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private UserService userService;

    @Override
    public UserDTO getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        return userService.updateById(ConvertBeanUtils.convert(userDTO, User.class));
    }

    @Override
    public boolean insertUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        return userService.save(ConvertBeanUtils.convert(userDTO, User.class));
    }
}
