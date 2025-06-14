package com.aiolos.live.living.provider.service.impl;

import com.aiolos.common.enums.base.BoolEnum;
import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.common.utils.PageConvertUtil;
import com.aiolos.live.common.wrapper.PageModel;
import com.aiolos.live.common.wrapper.PageResult;
import com.aiolos.live.living.dto.LivingRoomListDTO;
import com.aiolos.live.living.dto.StreamingDTO;
import com.aiolos.live.living.provider.service.LiveLivingRoomService;
import com.aiolos.live.living.vo.LivingRoomVO;
import com.aiolos.live.model.po.LivingRoom;
import com.aiolos.live.model.po.LivingRoomRecord;
import com.aiolos.live.service.LivingRoomRecordService;
import com.aiolos.live.service.LivingRoomService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LiveLivingRoomServiceImpl implements LiveLivingRoomService {

    @Autowired
    private LivingRoomService livingRoomService;
    @Autowired
    private LivingRoomRecordService livingRoomRecordService;
    
    @Override
    public boolean startStreaming(StreamingDTO dto) {
        LivingRoom livingRoom = ConvertBeanUtil.convert(dto, LivingRoom::new);
        livingRoom.setStatus(BoolEnum.YES.getValue());
        return livingRoomService.save(livingRoom);
    }

    @Override
    @Transactional
    public void stopStreaming(StreamingDTO dto) {
        LivingRoom livingRoom = livingRoomService.lambdaQuery().eq(LivingRoom::getId, dto.getRoomId()).eq(LivingRoom::getStreamerId, dto.getStreamerId()).one();
        if (livingRoom == null) {
            return;
        }
        LivingRoomRecord livingRoomRecord = ConvertBeanUtil.convert(livingRoom, LivingRoomRecord::new);
        livingRoomRecord.setId(null);
        if (livingRoomRecordService.save(livingRoomRecord)) {
            livingRoomService.removeById(dto.getRoomId());
        }
    }

    @Override
    public PageResult<LivingRoomVO> queryLivingRoomList(PageModel<LivingRoomListDTO> model) {
        LambdaQueryWrapper<LivingRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoom::getStatus, BoolEnum.YES.getValue());
        IPage<LivingRoom> page = livingRoomService.page(model.getPage(LivingRoom.class), queryWrapper);
        return PageConvertUtil.convert(page, LivingRoomVO.class);
    }
}
