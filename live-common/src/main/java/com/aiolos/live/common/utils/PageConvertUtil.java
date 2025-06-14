package com.aiolos.live.common.utils;

import com.aiolos.live.common.wrapper.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.stream.Collectors;

public class PageConvertUtil<T> {

    public static <T, R> PageResult<R> convert(IPage<T> page, Class<R> targetClass) {
        PageResult<R> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        result.setRecords(convertRecords(page.getRecords(), targetClass));
        return result;
    }

    private static <T, R> List<R> convertRecords(List<T> records, Class<R> targetClass) {
        return records.stream()
                .map(record -> convertRecord(record, targetClass))
                .collect(Collectors.toList());
    }

    private static <T, R> R convertRecord(T source, Class<R> targetClass) {
        try {
            R target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert target class", e);
        }
    }
    
    public static <T> PageResult<T> convert(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        result.setRecords(page.getRecords());
        return result;
    }

    public static <T, R> PageResult<R> convert(PageResult<T> source, Class<R> targetClass) {
        PageResult<R> target = new PageResult<>();
        target.setTotal(source.getTotal());
        target.setSize(source.getSize());
        target.setCurrent(source.getCurrent());
        target.setRecords(convertRecords(source.getRecords(), targetClass));
        return target;
    }
}
