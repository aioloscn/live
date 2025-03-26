package com.aiolos.live.id.generator.provider.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.aiolos.common.exception.utils.ExceptionUtil;
import com.aiolos.live.enums.ServiceExceptionEnum;
import com.aiolos.live.id.generator.provider.bo.LocalNonSeqIdBO;
import com.aiolos.live.id.generator.provider.bo.LocalSeqIdBO;
import com.aiolos.live.id.generator.provider.service.LiveIdGeneratorService;
import com.aiolos.live.model.po.IdGenerateConfigPO;
import com.aiolos.live.service.IdGenerateConfigService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class LiveIdGeneratorServiceImpl implements LiveIdGeneratorService {

    @Resource
    private IdGenerateConfigService idGenerateConfigService;

    private static final float UPDATE_RATE = 0.75f;
    private Map<Integer, LocalSeqIdBO> localSeqIdMap = new ConcurrentHashMap<>();
    private Map<Integer, LocalNonSeqIdBO> localNonSeqIdMap = new ConcurrentHashMap<>();
    private Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap<>();
    // 不同配置可以并发更新
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("id-generator-thread-" + thread.getId());
                return thread;
            });

    @PostConstruct
    public void init() {
        List<IdGenerateConfigPO> idGenerateConfigList = idGenerateConfigService.list();
        idGenerateConfigList.forEach(po -> {
            tryUpdateSeqId(po);
            // 每个配置都初始化一个线程信号量，防止并发更新
            semaphoreMap.put(po.getId(), new Semaphore(1));
        });
    }

    @Override
    public Long getSeqId(Integer id) {
        if (id == null) {
            log.error("[getSeqId] id is null");
            return null;
        }
        LocalSeqIdBO localSeqIdBO = localSeqIdMap.get(id);
        if (localSeqIdBO == null) {
            log.error("[getSeqId] get local seq id but empty, config id: {}", id);
            return null;
        }

        long returnId = localSeqIdBO.getCurrentNum().getAndIncrement();
        if (returnId >= localSeqIdBO.getNextThreshold()) {
            log.error("[getSeqId] id is over limit, config id: {}", id);
            return null;
        }
        refreshLocalSeqId(localSeqIdBO);
        return returnId;
    }

    @Override
    public Long getNonSeqId(Integer id) {
        if (id == null) {
            log.error("[getNonSeqId] id is null");
            return null;
        }
        LocalNonSeqIdBO localNonSeqIdBO = localNonSeqIdMap.get(id);
        if (localNonSeqIdBO == null) {
            log.error("[getNonSeqId] get local non-seq id but empty, config id: {}", id);
            return null;
        }
        Long nonSeqId = localNonSeqIdBO.getIdQueue().poll();
        if (nonSeqId >= localNonSeqIdBO.getNextThreshold()) {
            log.error("[getNonSeqId] id is over limit, config id: {}", id);
            return null; 
        }
        refreshLocalNonSeqId(localNonSeqIdBO);
        return nonSeqId;
    }

    private void refreshLocalSeqId(LocalSeqIdBO bo) {
        long step = bo.getNextThreshold() - bo.getCurrentStart();
        if (bo.getCurrentNum().get() - bo.getCurrentStart() > step * UPDATE_RATE) {
            asyncUpdateIdConfig(bo.getId());
        }
    }

    private void refreshLocalNonSeqId(LocalNonSeqIdBO bo) {
        long step = bo.getNextThreshold() - bo.getCurrentStart();
        if (bo.getIdQueue().size() < step * (1 - UPDATE_RATE)) {
            asyncUpdateIdConfig(bo.getId());
        }
    }

    private void asyncUpdateIdConfig(int id) {
        Semaphore semaphore = semaphoreMap.get(id);
        if (semaphore == null) {
            log.error("refreshLocalSeqId semaphore is null");
            return;
        }
        boolean acquire = semaphore.tryAcquire();
        if (acquire) {
            log.info("尝试将config id: {}进行本地同步", id);
            threadPoolExecutor.execute(() -> {
                try {
                    IdGenerateConfigPO po = idGenerateConfigService.getById(id);
                    if (po == null) {
                        log.error("config id: {}配置不存在", id);
                        return;
                    }
                    tryUpdateSeqId(po);
                } catch (Exception e) {
                    log.error("更新id配置失败, config id: {}", id, e);
                } finally {
                    semaphore.release();
                    log.info("config id: {}进行本地同步完成", id);
                }
            });
        }
    }

    private void tryUpdateSeqId(IdGenerateConfigPO po) {
        int retry = 0;
        boolean updated = false;
        while (retry < 5 && !updated) {
            retry++;
            Long nextThreshold = po.getNextThreshold();
            Long currentStart = po.getCurrentStart();
            updated = idGenerateConfigService.lambdaUpdate()
                    .set(IdGenerateConfigPO::getNextThreshold, nextThreshold + po.getStep())
                    .set(IdGenerateConfigPO::getCurrentStart, currentStart + po.getStep())
                    .set(IdGenerateConfigPO::getVersion, po.getVersion() + 1)
                    .eq(IdGenerateConfigPO::getId, po.getId())
                    .eq(IdGenerateConfigPO::getVersion, po.getVersion())
                    .update();
            if (!updated) {

                ThreadUtil.sleep(Math.pow(2, retry) * 50);
                po = idGenerateConfigService.getById(po.getId());
            } else {
                
                if (po.getIsSeq() == 1) {
                    LocalSeqIdBO bo = new LocalSeqIdBO();
                    bo.setId(po.getId());
                    bo.setCurrentNum(new AtomicLong(currentStart));
                    bo.setNextThreshold(nextThreshold);
                    bo.setCurrentStart(currentStart);
                    localSeqIdMap.put(po.getId(), bo);
                } else {
                    LocalNonSeqIdBO bo = new LocalNonSeqIdBO();
                    bo.setId(po.getId());
                    bo.setNextThreshold(nextThreshold);
                    bo.setCurrentStart(currentStart);
                    List<Long> idList = new ArrayList<>();
                    for (long i = currentStart; i < nextThreshold; i++) {
                        idList.add(i);
                    }
                    Collections.shuffle(idList);
                    ConcurrentLinkedQueue<Long> idQueue = new ConcurrentLinkedQueue<>(idList);
                    bo.setIdQueue(idQueue);
                    localNonSeqIdMap.put(po.getId(), bo);
                }
            }
        }

        if (!updated) {
            ExceptionUtil.throwException(ServiceExceptionEnum.GET_ID_SECTION_ERROR);
        }
    }
}
