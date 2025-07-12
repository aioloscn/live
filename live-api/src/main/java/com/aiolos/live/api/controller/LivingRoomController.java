package com.aiolos.live.api.controller;

import com.aiolos.common.enums.error.ErrorEnum;
import com.aiolos.common.exception.util.ExceptionUtil;
import com.aiolos.common.model.ContextInfo;
import com.aiolos.live.api.bo.LivingRoomListBO;
import com.aiolos.live.api.service.HomeLivingRoomService;
import com.aiolos.live.api.vo.LivingRoomListVO;
import com.aiolos.live.api.vo.ApiLivingRoomVO;
import com.aiolos.common.wrapper.PageModel;
import com.aiolos.common.wrapper.PageResult;
import com.aiolos.live.enums.exceptions.LivingRoomExceptionEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/living-room")
@Tag(name = "直播服务")
public class LivingRoomController {
    
    @Autowired
    private HomeLivingRoomService  homeLivingRoomService;
    
    @PostMapping("/start-streaming")
    public ApiLivingRoomVO startStreaming(Integer type) {
        if (type == null) {
            ExceptionUtil.throwException(LivingRoomExceptionEnum.START_STREAMING_TYPE_ERROR);
        }
        Long roomId = homeLivingRoomService.startStreaming(type);
        if (roomId == null) {
            ExceptionUtil.throwException(LivingRoomExceptionEnum.START_STREAMING_ERROR);
        }
        ApiLivingRoomVO vo = new ApiLivingRoomVO();
        vo.setRoomId(roomId);
        return vo;
    }
    
    @PostMapping("/stop-streaming")
    public void stopStreaming(Long roomId) {
        if (roomId == null) {
            ExceptionUtil.throwException(ErrorEnum.BIND_EXCEPTION_ERROR);
        }
        homeLivingRoomService.stopStreaming(roomId);
    }
    
    @PostMapping("/list")
    public PageResult<LivingRoomListVO> queryLivingRoomList(@RequestBody PageModel<LivingRoomListBO> model) {
        return homeLivingRoomService.queryLivingRoomList(model);
    }
    
    @PostMapping("/anchor-config")
    public ApiLivingRoomVO anchorConfig(Long roomId) {
        Long userId = ContextInfo.getUserId();
        if (roomId == null) {
            return null;
        }
        return homeLivingRoomService.queryByRoomId(userId, roomId);
    }
}
