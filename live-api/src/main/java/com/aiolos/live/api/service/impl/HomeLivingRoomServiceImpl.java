package com.aiolos.live.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aiolos.common.model.ContextInfo;
import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.api.bo.LivingRoomListBO;
import com.aiolos.live.api.service.HomeLivingRoomService;
import com.aiolos.live.api.vo.ApiLivingRoomVO;
import com.aiolos.live.api.vo.LivingRoomListVO;
import com.aiolos.live.common.utils.PageConvertUtil;
import com.aiolos.live.common.wrapper.PageModel;
import com.aiolos.live.common.wrapper.PageResult;
import com.aiolos.live.living.dto.LivingRoomListDTO;
import com.aiolos.live.living.dto.StreamingDTO;
import com.aiolos.live.living.interfaces.LivingRoomRpc;
import com.aiolos.live.living.vo.LivingRoomVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HomeLivingRoomServiceImpl implements HomeLivingRoomService {
    
    @DubboReference
    private LivingRoomRpc livingRoomRpc;
    
    @Override
    public Long startStreaming(Integer type) {
        StreamingDTO dto = new StreamingDTO();
        dto.setStreamerId(ContextInfo.getUserId());
        dto.setRoomName("测试直播间");
        dto.setType(type);
        dto.setCover(ContextInfo.getAvatar());
        return livingRoomRpc.startStreaming(dto);
    }

    @Override
    public void stopStreaming(Long roomId) {
        StreamingDTO dto = new StreamingDTO();
        dto.setStreamerId(ContextInfo.getUserId());
        dto.setRoomId(roomId);
        livingRoomRpc.stopStreaming(dto);
    }

    @Override
    public PageResult<LivingRoomListVO> queryLivingRoomList(PageModel<LivingRoomListBO> model) {
        PageModel<LivingRoomListDTO> newModel = model.convert(bo -> ConvertBeanUtil.convert(bo, LivingRoomListDTO.class));
        PageResult<LivingRoomVO> result = livingRoomRpc.queryLivingRoomList(newModel);
        return PageConvertUtil.convert(result, LivingRoomListVO.class);
    }

    @Override
    public ApiLivingRoomVO queryByRoomId(Long userId, Long roomId) {
        ApiLivingRoomVO vo = new ApiLivingRoomVO();
        vo.setRoomId(roomId);
        vo.setUserId(userId);
        vo.setNickName(ContextInfo.getNickName());
        vo.setAvatar(ContextInfo.getAvatar());
        LivingRoomVO livingRoomVO = livingRoomRpc.queryByRoomId(roomId);
        if (livingRoomVO != null && livingRoomVO.getStreamerId() != null) {
            BeanUtil.copyProperties(livingRoomVO, vo);
        }
        return vo;
    }
}
