package com.aiolos.live.api.service.impl;

import com.aiolos.badger.enums.UserTagEnum;
import com.aiolos.badger.user.api.UserApi;
import com.aiolos.badger.user.api.UserTagApi;
import com.aiolos.badger.user.dto.UserDTO;
import com.aiolos.live.api.service.HomePageService;
import com.aiolos.live.api.vo.HomePageVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HomePageServiceImpl implements HomePageService {

    @DubboReference
    private UserApi userApi;
    @DubboReference
    private UserTagApi userTagApi;
    
    @Override
    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = userApi.getUserById(userId);
        HomePageVO vo = new HomePageVO();
        vo.setUserId(userId);
        if (userDTO != null) {
            vo.setNickName(userDTO.getNickName());
            vo.setAvatar(userDTO.getAvatar());
            vo.setShowStartLivingBtn(userTagApi.checkTag(userId, UserTagEnum.IS_LIVE_STREAMER));
        }
        return vo;
    }
}
