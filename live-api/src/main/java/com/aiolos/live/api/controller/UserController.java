package com.aiolos.live.api.controller;

import com.aiolos.badger.user.api.UserApi;
import com.aiolos.badger.user.dto.UserDTO;
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
    private UserApi userApi;

    @GetMapping("/get-user-by-id")
    public UserDTO getUserById(Long userId) {
        return userApi.getUserById(userId);
    }
    
    @PostMapping("/insert-user")
    public void insertUser(@RequestBody UserDTO userDTO) {
        userApi.insertUser(userDTO);
    }
    
    @PostMapping("/update-user-info")
    public void updateUserInfo(@RequestBody UserDTO userDTO) {
        userApi.updateUserInfo(userDTO);
    }

    @PostMapping("/batch-query-user-info")
    public Map<Long, UserDTO> batchQueryUserInfo(@RequestBody List<Long> userIds) {
        return userApi.batchQueryUserInfo(userIds);
    }
}
