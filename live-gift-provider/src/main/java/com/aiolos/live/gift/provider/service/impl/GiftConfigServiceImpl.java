package com.aiolos.live.gift.provider.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.common.enums.base.BoolEnum;
import com.aiolos.common.util.ConvertBeanUtil;
import com.aiolos.live.common.keys.builder.GiftProviderRedisKeyBuilder;
import com.aiolos.live.gift.api.dto.GiftConfigDTO;
import com.aiolos.live.gift.provider.service.GiftConfigService;
import com.aiolos.live.model.po.LivingGiftConfig;
import com.aiolos.live.service.LivingGiftConfigService;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GiftConfigServiceImpl implements GiftConfigService {
    
    @Resource
    private LivingGiftConfigService livingGiftConfigService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderRedisKeyBuilder giftProviderRedisKeyBuilder;
    
    @Override
    public GiftConfigDTO getGiftById(Long id) {
        String key = giftProviderRedisKeyBuilder.buildGiftConfigKey(id);
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj != null) {
            GiftConfigDTO dto = ConvertBeanUtil.convert(obj, GiftConfigDTO.class);
            if (dto.getId() != null) {
                redisTemplate.expire(key, 60, TimeUnit.MINUTES);
                return dto;
            }
            // 空值缓存
            return null;
        }
        
        LivingGiftConfig giftConfig = livingGiftConfigService.lambdaQuery().eq(LivingGiftConfig::getId, id).eq(LivingGiftConfig::getStatus, BoolEnum.YES.getValue()).one();
        if (giftConfig != null) {
            GiftConfigDTO dto = ConvertBeanUtil.convert(giftConfig, GiftConfigDTO.class);
            redisTemplate.opsForValue().set(key, dto, 60, TimeUnit.MINUTES);
            return dto;
        }
        
        redisTemplate.opsForValue().set(key, new GiftConfigDTO(), 1, TimeUnit.MINUTES);
        return null;
    }

    @Override
    public List<GiftConfigDTO> queryGiftList() {
        String key = giftProviderRedisKeyBuilder.buildGiftConfigListKey();
        List<GiftConfigDTO> giftDtoList = redisTemplate.opsForList().range(key, 0, -1).stream().map(o -> ConvertBeanUtil.convert(o, GiftConfigDTO.class)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(giftDtoList)) {
            if (giftDtoList.get(0).getId() != null) {
                redisTemplate.expire(key, 60, TimeUnit.MINUTES);
                return giftDtoList;
            }
            return Lists.newArrayList();
        }

        List<LivingGiftConfig> giftList = livingGiftConfigService.list();
        if (CollectionUtil.isNotEmpty(giftList)) {
            giftDtoList = ConvertBeanUtil.convertList(giftList, GiftConfigDTO.class);
            redisTemplate.opsForList().leftPushAll(key, giftDtoList.toArray(new GiftConfigDTO[0]));
            redisTemplate.expire(key, 60, TimeUnit.MINUTES);
            return giftDtoList;
        }

        redisTemplate.opsForList().rightPush(key, new GiftConfigDTO());
        redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        return Lists.newArrayList();
    }

    @Override
    public void insertGift(GiftConfigDTO dto) {
        if (dto == null) return;
        LivingGiftConfig giftConfig = ConvertBeanUtil.convert(dto, LivingGiftConfig.class);
        giftConfig.setId(null);
        giftConfig.setStatus(true);
        livingGiftConfigService.save(giftConfig);
        redisTemplate.delete(giftProviderRedisKeyBuilder.buildGiftConfigListKey());
    }

    @Override
    public void updateGift(GiftConfigDTO dto) {
        if (dto == null || dto.getId() == null) return;
        LivingGiftConfig giftConfig = ConvertBeanUtil.convert(dto, LivingGiftConfig.class);
        livingGiftConfigService.updateById(giftConfig);
        redisTemplate.delete(giftProviderRedisKeyBuilder.buildGiftConfigKey(dto.getId()));
        redisTemplate.delete(giftProviderRedisKeyBuilder.buildGiftConfigListKey());
    }
}
