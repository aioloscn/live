package com.aiolos.live.living.provider.tasks;

import cn.hutool.core.collection.CollectionUtil;
import com.aiolos.common.enums.base.BoolEnum;
import com.aiolos.common.util.ConvertBeanUtil;
import com.aiolos.live.common.keys.builder.LivingRoomRedisKeyBuilder;
import com.aiolos.live.enums.LivingRoomTypeEnum;
import com.aiolos.live.living.vo.LivingRoomVO;
import com.aiolos.live.model.po.LivingRoom;
import com.aiolos.live.service.LivingRoomService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class LivingRoomTask {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingRoomRedisKeyBuilder livingRoomRedisKeyBuilder;
    @Autowired
    private LivingRoomService livingRoomService;

    @Value("${task.refresh-living-room.ip}")
    private String dubboIpToRegistry;

    @Scheduled(cron = "*/1 * * * * ?")
    public void refreshLivingRoomCache() {
        // 尝试获取分布式锁，1秒钟过期
        String lockKey = "live:livingRoom:lock";
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, dubboIpToRegistry, 1, TimeUnit.SECONDS);
        if (locked) {
            try {
                refreshDbToRedis(LivingRoomTypeEnum.DEFAULT_LIVING_ROOM.getCode());
                refreshDbToRedis(LivingRoomTypeEnum.PK_LIVING_ROOM.getCode());
            } finally {
                // 查询当前节点是否持有锁和delete是两个独立的操作，这期间锁会过期，会导致误删，存在原子性漏洞
                /*if (redisTemplate.opsForValue().get(lockKey).equals(dubboIpToRegistry))
                    redisTemplate.delete(lockKey);*/
                
                // 使用LUA脚本释放锁
                String script =
                        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "   return redis.call('del', KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end";
                redisTemplate.execute(
                        new DefaultRedisScript<>(script, Long.class),
                        Collections.singletonList(lockKey), // 锁key，对应KEYS[1]
                        dubboIpToRegistry   // 可变参数，对应ARGV[1]，保证是同一个锁持有者
                );
            }
        }
    }

    private void refreshDbToRedis(Integer type) {
        String key = livingRoomRedisKeyBuilder.buildLivingRoomListKey(type);
        List<LivingRoom> list = livingRoomService.lambdaQuery()
                .eq(LivingRoom::getStatus, BoolEnum.YES.getValue())
                .eq(LivingRoom::getType, type)
                .orderByDesc(LivingRoom::getWatchCount)
                .list();
        if (CollectionUtil.isEmpty(list)) {
            redisTemplate.delete(key);
            return;
        }
        List<LivingRoomVO> livingRooms = ConvertBeanUtil.convertList(list, LivingRoomVO.class);
        String tempKey = key + "_temp" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        redisTemplate.opsForList().rightPushAll(tempKey, livingRooms.toArray(new LivingRoomVO[0]));    // 必须以数组格式存储，否则整个list会被视为一个元素去存储
        // 先缓存到没人访问的temp中，再重命名，减少阻塞
        redisTemplate.rename(tempKey, key);
    }
}
