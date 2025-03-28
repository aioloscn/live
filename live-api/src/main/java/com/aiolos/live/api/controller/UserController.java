package com.aiolos.live.api.controller;

import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.interfaces.UserRpc;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "用户服务")
public class UserController {

    @DubboReference
    private UserRpc userRpc;

    @GetMapping("/get-user-by-id")
    public UserDTO getUserById(Long userId) {
        return userRpc.getUserById(userId);
    }
    
    @PostMapping("/insert-user")
    public void insertUser(@RequestBody UserDTO userDTO) {
        userRpc.insertUser(userDTO);
    }
    
    @PostMapping("/update-user-info")
    public void updateUserInfo(@RequestBody UserDTO userDTO) {
        userRpc.updateUserInfo(userDTO);
    }

    @PostMapping("/batch-query-user-info")
    public Map<Long, UserDTO> batchQueryUserInfo(@RequestBody List<Long> userIds) {
        return userRpc.batchQueryUserInfo(userIds);
    }
}
