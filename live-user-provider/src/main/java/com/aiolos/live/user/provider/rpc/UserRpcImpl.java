package com.aiolos.live.user.provider.rpc;

import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.interfaces.IUserRpc;
import com.aiolos.live.user.provider.service.LiveUserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;

@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private LiveUserService liveUserService;
    
    @Override
    public UserDTO getUserById(Long userId) {
        return liveUserService.getUserById(userId);
    }

    @Override
    public void insertUser(UserDTO userDTO) {
        liveUserService.insertUser(userDTO);
    }

    @Override
    public void updateUserInfo(UserDTO userDTO) {
        liveUserService.updateUserInfo(userDTO);
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIds) {
        return liveUserService.batchQueryUserInfo(userIds);
    }
}
