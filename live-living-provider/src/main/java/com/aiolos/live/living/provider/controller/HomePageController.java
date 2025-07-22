package com.aiolos.live.living.provider.controller;

import com.aiolos.badger.enums.UserTagEnum;
import com.aiolos.badger.user.api.UserApi;
import com.aiolos.badger.user.api.UserTagApi;
import com.aiolos.badger.user.dto.UserDTO;
import com.aiolos.common.model.ContextInfo;
import com.aiolos.live.living.provider.vo.HomePageVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/home")
@Tag(name = "首页")
public class HomePageController {
    
    @DubboReference
    private UserApi userApi;
    @DubboReference
    private UserTagApi userTagApi;
    
    @PostMapping("/init-page")
    public HomePageVO initPage() {
        Long userId = ContextInfo.getUserId();
        log.info("用户Id: {}", userId);
        HomePageVO vo = new HomePageVO();
        if (userId != null && !ContextInfo.isAnonymous()) {
            UserDTO userDTO = userApi.getUserById(userId);
            vo.setUserId(userId);
            if (userDTO != null) {
                vo.setNickName(userDTO.getNickName());
                vo.setAvatar(userDTO.getAvatar());
                vo.setShowStartLivingBtn(userTagApi.checkTag(userId, UserTagEnum.IS_LIVE_STREAMER));
            }
            vo.setLoginStatus(true);
        }
        return vo;
    }
}
