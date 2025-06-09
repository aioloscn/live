package com.aiolos.live.msg.provider.service.impl;

import com.aiolos.common.enums.errors.ErrorEnum;
import com.aiolos.common.exception.utils.ExceptionUtil;
import com.aiolos.common.model.response.CommonResponse;
import com.aiolos.live.common.keys.builder.MsgProviderRedisKeyBuilder;
import com.aiolos.live.common.keys.builder.common.MsgProviderCommonRedisKeyBuilder;
import com.aiolos.live.enums.MsgSendResultEnum;
import com.aiolos.live.model.po.SmsRecord;
import com.aiolos.live.msg.provider.config.MsgThreadPoolManager;
import com.aiolos.live.msg.provider.service.LiveSmsService;
import com.aiolos.live.service.SmsRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LiveSmsServiceImpl implements LiveSmsService {

    @Resource
    private SmsRecordService smsRecordService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MsgProviderRedisKeyBuilder msgProviderRedisKeyBuilder;
    @Resource
    private MsgProviderCommonRedisKeyBuilder msgProviderCommonRedisKeyBuilder;
    
    @Transactional
    @Override
    public CommonResponse sendSms(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() != 11) {
            return CommonResponse.error(MsgSendResultEnum.PARAM_ERROR.getCode(), MsgSendResultEnum.PARAM_ERROR.getDesc());
        }

        if (!redisTemplate.opsForValue().setIfAbsent(msgProviderRedisKeyBuilder.buildPreventRepeatSendingKey(phone), 1, 60, TimeUnit.SECONDS)) {
            ExceptionUtil.throwException(ErrorEnum.REPEAT_SENDING_SMS_CODE);
        }

        // 生成6位验证码，有效期5分钟，1分钟内不能重复发送，存储到redis
        int code = RandomUtils.secure().randomInt(100000, 999999);
        String smsRedisKey = msgProviderCommonRedisKeyBuilder.buildSmsLoginCodeKey(phone);
        redisTemplate.opsForValue().set(smsRedisKey, code, 300, TimeUnit.SECONDS);
        
        // 发送短信
        MsgThreadPoolManager.commonAsyncPool.execute(() -> {
            // TODO 接收SmsUtil发送结果，然后保存记录
            // 同一个类内部的非事务方法调用本类中的事务方法，事务失效
            // 数据库操作不能放线程池中，不然主线程抛出异常无法回滚异步线程中的数据库操作
            log.info("已向手机号: {} 发送验证码: {}", phone, code);
        });
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setPhone(phone);
        smsRecord.setCode(code);
        smsRecordService.save(smsRecord);
        return CommonResponse.ok(MsgSendResultEnum.SUCCESS);
    }
}
