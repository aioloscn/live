package com.aiolos.live.api.controller;

import com.aiolos.common.model.ContextInfo;
import com.aiolos.live.api.service.HomePageService;
import com.aiolos.live.api.vo.HomePageVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/home")
@Tag(name = "首页")
public class HomePageController {
    
    @Autowired
    private HomePageService homePageService;
    
    @PostMapping("/init-page")
    public HomePageVO initPage() {
        Long userId = ContextInfo.getUserId();
        log.info("用户Id: {}", userId);
        HomePageVO vo = new HomePageVO();
        if (userId != null && !ContextInfo.isAnonymous()) {
            vo = homePageService.initPage(userId);
            vo.setLoginStatus(true);
        }
        return vo;
    }
}
