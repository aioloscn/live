package com.aiolos.live.living.provider.controller;

import cn.hutool.core.bean.BeanUtil;
import com.aiolos.common.enums.error.ErrorEnum;
import com.aiolos.common.exception.util.ExceptionUtil;
import com.aiolos.common.model.ContextInfo;
import com.aiolos.common.wrapper.PageModel;
import com.aiolos.common.wrapper.PageResult;
import com.aiolos.live.enums.exceptions.LivingRoomExceptionEnum;
import com.aiolos.live.living.provider.bo.LivingRoomListBO;
import com.aiolos.live.living.provider.bo.StreamingBO;
import com.aiolos.live.living.provider.service.LiveLivingRoomService;
import com.aiolos.live.living.vo.LivingRoomVO;
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
    private LiveLivingRoomService liveLivingRoomService;
    
    @PostMapping("/start-streaming")
    public LivingRoomVO startStreaming(Integer type) {
        if (type == null) {
            ExceptionUtil.throwException(LivingRoomExceptionEnum.START_STREAMING_TYPE_ERROR);
        }

        StreamingBO bo = new StreamingBO();
        bo.setStreamerId(ContextInfo.getUserId());
        bo.setRoomName("测试直播间");
        bo.setType(type);
        bo.setCover(ContextInfo.getAvatar());
        Long roomId = liveLivingRoomService.startStreaming(bo);
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

        StreamingBO bo = new StreamingBO();
        bo.setRoomId(roomId);
        bo.setStreamerId(ContextInfo.getUserId());
        liveLivingRoomService.stopStreaming(bo);
    }
    
    @PostMapping("/list")
    public PageResult<LivingRoomVO> queryLivingRoomList(@RequestBody PageModel<LivingRoomListBO> model) {
        return liveLivingRoomService.queryLivingRoomList(model);
    }
    
    @PostMapping("/anchor-config")
    public LivingRoomVO anchorConfig(Long roomId) {
        Long userId = ContextInfo.getUserId();
        if (roomId == null) {
            return null;
        }
        LivingRoomVO vo = new LivingRoomVO();
        vo.setRoomId(roomId);
        vo.setUserId(userId);
        vo.setNickName(ContextInfo.getNickName());
        vo.setAvatar(ContextInfo.getAvatar());
        LivingRoomVO livingRoomVO = liveLivingRoomService.queryByRoomId(roomId);
        if (livingRoomVO != null && livingRoomVO.getStreamerId() != null) {
            BeanUtil.copyProperties(livingRoomVO, vo);
        }
        return vo;
    }
}
