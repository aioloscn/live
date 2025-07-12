package com.aiolos.live.living.provider.service.impl;

import com.aiolos.common.enums.base.BoolEnum;
import com.aiolos.common.util.ConvertBeanUtil;
import com.aiolos.common.util.PageConvertUtil;
import com.aiolos.live.common.keys.builder.LivingRoomRedisKeyBuilder;
import com.aiolos.common.wrapper.Page;
import com.aiolos.common.wrapper.PageModel;
import com.aiolos.common.wrapper.PageResult;
import com.aiolos.live.living.dto.LivingRoomListDTO;
import com.aiolos.live.living.dto.LivingRoomUserDTO;
import com.aiolos.live.living.dto.StreamingDTO;
import com.aiolos.live.living.provider.service.LiveLivingRoomService;
import com.aiolos.live.living.vo.LivingRoomUserVO;
import com.aiolos.live.living.vo.LivingRoomVO;
import com.aiolos.live.model.po.LivingRoom;
import com.aiolos.live.model.po.LivingRoomRecord;
import com.aiolos.live.service.LivingRoomRecordService;
import com.aiolos.live.service.LivingRoomService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LiveLivingRoomServiceImpl implements LiveLivingRoomService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingRoomRedisKeyBuilder livingRoomRedisKeyBuilder;
    @Autowired
    private LivingRoomService livingRoomService;
    @Autowired
    private LivingRoomRecordService livingRoomRecordService;
    
    @Override
    public Long startStreaming(StreamingDTO dto) {
        LivingRoom livingRoom = ConvertBeanUtil.convert(dto, LivingRoom.class);
        livingRoom.setStatus(BoolEnum.YES.getValue());
        livingRoomService.save(livingRoom);
        // 防止之前有空值缓存
        redisTemplate.delete(livingRoomRedisKeyBuilder.buildLivingRoomObjKey(livingRoom.getId()));
        return livingRoom.getId();
    }

    @Override
    @Transactional
    public void stopStreaming(StreamingDTO dto) {
        LivingRoom livingRoom = livingRoomService.lambdaQuery().eq(LivingRoom::getId, dto.getRoomId()).eq(LivingRoom::getStreamerId, dto.getStreamerId()).one();
        if (livingRoom == null) {
            return;
        }
        LivingRoomRecord livingRoomRecord = ConvertBeanUtil.convert(livingRoom, LivingRoomRecord.class);
        livingRoomRecord.setId(null);
        if (livingRoomRecordService.save(livingRoomRecord)) {
            livingRoomService.removeById(dto.getRoomId());
            redisTemplate.delete(livingRoomRedisKeyBuilder.buildLivingRoomObjKey(dto.getRoomId()));
        }
    }

    @Override
    public PageResult<LivingRoomVO> queryLivingRoomList(PageModel<LivingRoomListDTO> model) {
        String key = livingRoomRedisKeyBuilder.buildLivingRoomListKey(model.getData().getType());
        long current = model.getCurrent();
        long size = model.getSize();
        Long total = redisTemplate.opsForList().size(key);
        if (total == 0) {
            return PageConvertUtil.convert(model.getPage(LivingRoomVO.class));
        }
        List<Object> resultList = redisTemplate.opsForList().range(key, (current - 1) * size, current * size - 1);
        List<LivingRoomVO> livingRooms = ConvertBeanUtil.convertList(resultList, LivingRoomVO.class);
        Page<LivingRoomVO> page = model.getPage(LivingRoomVO.class);
        page.setTotal(total);
        page.setRecords(livingRooms);
        return PageConvertUtil.convert(page);
    }

    @Override
    public LivingRoomVO queryByRoomId(Long roomId) {
        if (roomId == null) return null;
        String key = livingRoomRedisKeyBuilder.buildLivingRoomObjKey(roomId);
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj != null) {
            LivingRoomVO vo = ConvertBeanUtil.convert(obj, LivingRoomVO.class);
            return vo.getId() == null ? null : vo;
        }
        LivingRoom livingRoom = livingRoomService.lambdaQuery().eq(LivingRoom::getId, roomId).one();
        if (livingRoom == null) {
            // 防止缓存击穿
            redisTemplate.opsForValue().set(key, new LivingRoomVO(), 1, TimeUnit.MINUTES);
            return null;
        }
        LivingRoomVO vo = ConvertBeanUtil.convert(livingRoom, LivingRoomVO.class);
        redisTemplate.opsForValue().set(key, vo, 30, TimeUnit.MINUTES);
        return vo;
    }

    @Override
    public LivingRoomUserVO queryLivingRoomUser(LivingRoomUserDTO dto) {
        String key = livingRoomRedisKeyBuilder.buildLivingRoomUserSetKey(dto.getRoomId(), dto.getAppId());
        Cursor<Object> cursor = redisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().match("*").count(500).build());
        LivingRoomUserVO vo = new LivingRoomUserVO();
        List<Long> userIds = new ArrayList<>();
        vo.setUserIds(userIds);
        while (cursor.hasNext()) {
            userIds.add(Long.parseLong(cursor.next().toString()));
        }
        return vo;
    }
}
