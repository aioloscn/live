package com.aiolos.live.api.controller;

import com.aiolos.common.model.ContextInfo;
import com.aiolos.common.model.response.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/home")
@Tag(name = "首页")
public class HomePageController {
    
    @PostMapping("/init-page")
    public CommonResponse initPage() {
        Long userId = ContextInfo.getUserId();
        log.info("用户Id: {}", userId);
        return CommonResponse.ok();
    }
}
