package com.aiolos.live.api.controller;

import com.aiolos.common.enums.errors.ErrorEnum;
import com.aiolos.common.exception.utils.ExceptionUtil;
import com.aiolos.live.api.bo.LivingRoomListBO;
import com.aiolos.live.api.service.HomeLivingRoomService;
import com.aiolos.live.api.vo.LivingRoomListVO;
import com.aiolos.live.api.vo.LivingRoomVO;
import com.aiolos.live.common.wrapper.PageModel;
import com.aiolos.live.common.wrapper.PageResult;
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
    public LivingRoomVO startStreaming(Integer type) {
        if (type == null) {
            ExceptionUtil.throwException(LivingRoomExceptionEnum.START_STREAMING_TYPE_ERROR);
        }
        Long roomId = homeLivingRoomService.startStreaming(type);
        if (roomId == null) {
            ExceptionUtil.throwException(LivingRoomExceptionEnum.START_STREAMING_ERROR);
        }
        LivingRoomVO vo = new LivingRoomVO();
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
}
