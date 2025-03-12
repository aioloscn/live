package com.aiolos.live.api.controller;

import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.interfaces.IUserRpc;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "用户服务")
public class UserController {

    @DubboReference
    private IUserRpc userRpc;

    @GetMapping("/getUserById")
    public UserDTO getUserById(Long userId) {
        return userRpc.getUserById(userId);
    }
}
