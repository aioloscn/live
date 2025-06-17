package com.aiolos.live.user.provider.rpc;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.interfaces.UserRpc;
import com.aiolos.live.user.provider.model.vo.UserVO;
import com.aiolos.live.user.provider.service.LiveUserService;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService
public class UserRpcImpl implements UserRpc {

    @Resource
    private LiveUserService liveUserService;

    @Override
    public String createToken(Long userId) {
        return liveUserService.createToken(userId);
    }

    @Override
    public UserDTO getUserIdByToken(String token) {
        return liveUserService.getUserIdByToken(token);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return ConvertBeanUtil.convert(liveUserService.getUserById(userId), UserDTO.class);
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
        Map<Long, UserVO> userVOMap = liveUserService.batchQueryUserInfo(userIds);
        if (CollectionUtil.isEmpty(userVOMap)) {
            return Maps.newHashMap();
        }
        return userVOMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ConvertBeanUtil.convert(entry.getValue(), UserDTO.class)));
    }
}
