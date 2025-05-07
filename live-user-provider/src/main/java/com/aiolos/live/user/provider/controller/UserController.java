package com.aiolos.live.user.provider.controller;

import com.aiolos.live.user.provider.model.bo.LoginBO;
import com.aiolos.live.user.provider.model.vo.UserVO;
import com.aiolos.live.user.provider.service.LiveUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "用户服务")
@AllArgsConstructor
public class UserController {

    private final LiveUserService liveUserService;

    @PostMapping("/login")
    public UserVO login(@RequestBody LoginBO loginBO, HttpServletResponse response) {
        return liveUserService.login(loginBO, response);
    }
}
