package com.aiolos.live.user.provider.rpc;

import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.interfaces.IUserRpc;
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
}
