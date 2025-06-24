package com.aiolos.live.user.provider.service.impl;

import com.aiolos.common.utils.ConvertBeanUtil;
import com.aiolos.live.common.constants.UserTagFieldNameConstants;
import com.aiolos.live.common.keys.builder.common.UserProviderCommonRedisKeyBuilder;
import com.aiolos.live.enums.UserTagEnum;
import com.aiolos.live.model.po.UserTag;
import com.aiolos.live.service.UserTagService;
import com.aiolos.live.user.provider.mq.producer.UpdateUserInfoProducer;
import com.aiolos.live.user.provider.service.LiveUserTagService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LiveUserTagServiceImpl implements LiveUserTagService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserProviderCommonRedisKeyBuilder userProviderCommonRedisKeyBuilder;
    @Autowired
    private UserTagService userTagService;
    @Autowired
    private UpdateUserInfoProducer updateUserInfoProducer;
    
    @Override
    public boolean setTag(Long userId, UserTagEnum userTagEnum) {

        String setNXKey = userProviderCommonRedisKeyBuilder.buildUserTagLockKey(userId);
        String setNXResult = redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer keySerializer = redisTemplate.getKeySerializer();
            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
            return (String) connection.execute("set",
                    keySerializer.serialize(setNXKey),
                    valueSerializer.serialize("-1"),
                    "NX".getBytes(StandardCharsets.UTF_8),
                    "EX".getBytes(StandardCharsets.UTF_8),
                    "3".getBytes(StandardCharsets.UTF_8));
        });
        if (!"ok".equalsIgnoreCase(setNXResult)) {
            log.error("{}获取redis锁失败", Thread.currentThread().getName());
            return false;
        }
        log.info("{}获取redis锁成功", Thread.currentThread().getName());

        boolean updated = true;
        UserTag userTag = getUserTagFromRedis(userId);
        if (userTag != null) {
            if (!checkTag(userTag, userTagEnum)) {
                userTag.setTagInfo01(userTag.getTagInfo01() | userTagEnum.getTag());
                updated = userTagService.update(userTag, Wrappers.lambdaUpdate(UserTag.class).eq(UserTag::getUserId, userId));

                redisTemplate.delete(userProviderCommonRedisKeyBuilder.buildUserTagKey(userId));
                log.info("修改用户{}的标签{}, result: {}", userId, userTagEnum.getDesc(), updated);
            }
        } else {
            userTag = new UserTag();
            userTag.setUserId(userId);
            userTag.setTagInfo01(userTagEnum.getTag());
            updated = userTagService.save(userTag);
            log.info("保存用户{}的标签{}, result: {}", userId, userTagEnum.getDesc(), updated);
        }
        redisTemplate.delete(setNXKey);
        return updated;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagEnum userTagEnum) {
        
        boolean updated = true;
        UserTag userTag = getUserTagFromRedis(userId);
        if (userTag != null) {
            if (checkTag(userTag, userTagEnum)) {
                
                // a &(~b) 对应位清零
                userTag.setTagInfo01(userTag.getTagInfo01() & ~userTagEnum.getTag());
                updated = userTagService.update(userTag, Wrappers.lambdaUpdate(UserTag.class).eq(UserTag::getUserId, userId));
                if (updated) {
                    redisTemplate.delete(userProviderCommonRedisKeyBuilder.buildUserTagKey(userId));
                    updateUserInfoProducer.deleteUserTagCache(userId);
                }
            }
        }
        return updated;
    }

    @Override
    public boolean checkTag(Long userId, UserTagEnum userTagEnum) {
        UserTag userTag = getUserTagFromRedis(userId);
        return checkTag(userTag, userTagEnum);
    }

    private boolean checkTag(UserTag userTag, UserTagEnum userTagEnum) {

        if (userTag == null || userTag.getUserId() == null || userTagEnum.getTag() <= 0)
            return false;
        
        if (userTagEnum.getFieldName().equals(UserTagFieldNameConstants.TAG_INFO_01) && userTag.getTagInfo01() > 0) {
            return (userTag.getTagInfo01() & userTagEnum.getTag()) == userTagEnum.getTag();
        } else if (userTagEnum.getFieldName().equals(UserTagFieldNameConstants.TAG_INFO_02) && userTag.getTagInfo02() > 0) {
            return (userTag.getTagInfo02() & userTagEnum.getTag()) == userTagEnum.getTag();
        } else if (userTagEnum.getFieldName().equals(UserTagFieldNameConstants.TAG_INFO_03) && userTag.getTagInfo03() > 0) {
            return (userTag.getTagInfo03() & userTagEnum.getTag()) == userTagEnum.getTag();
        }
        return false;
    }
    
    private UserTag getUserTagFromRedis(Long userId) {
        UserTag userTag = null;
        Object obj = redisTemplate.opsForValue().get(userProviderCommonRedisKeyBuilder.buildUserTagKey(userId));
        if (obj != null) {
            userTag = ConvertBeanUtil.convert(obj, UserTag.class);
        } else {
            userTag = userTagService.getOne(Wrappers.lambdaQuery(UserTag.class).eq(UserTag::getUserId, userId));
            if (userTag != null) {
                redisTemplate.opsForValue().set(userProviderCommonRedisKeyBuilder.buildUserTagKey(userId), userTag, 7, TimeUnit.DAYS);
            }
        }
        return userTag;
    }
}
