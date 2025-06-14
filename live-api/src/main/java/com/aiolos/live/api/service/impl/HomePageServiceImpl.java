package com.aiolos.live.api.service.impl;

import com.aiolos.live.api.service.HomePageService;
import com.aiolos.live.api.vo.HomePageVO;
import com.aiolos.live.enums.UserTagEnum;
import com.aiolos.live.user.dto.UserDTO;
import com.aiolos.live.user.interfaces.UserRpc;
import com.aiolos.live.user.interfaces.UserTagRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HomePageServiceImpl implements HomePageService {

    @DubboReference
    private UserRpc userRpc;
    @DubboReference
    private UserTagRpc userTagRpc;
    
    @Override
    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = userRpc.getUserById(userId);
        HomePageVO vo = new HomePageVO();
        vo.setUserId(userId);
        if (userDTO != null) {
            vo.setNickName(userDTO.getNickName());
            vo.setAvatar(userDTO.getAvatar());
            vo.setShowStartLivingBtn(userTagRpc.checkTag(userId, UserTagEnum.IS_LIVE_STREAMER));
        }
        return vo;
    }
}
