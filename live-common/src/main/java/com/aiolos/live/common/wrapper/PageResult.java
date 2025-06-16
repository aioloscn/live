package com.aiolos.live.common.wrapper;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

    private Long total;
    private Long size;
    private Long current;
    private List<T> records;
    // 不能用boolean，因为默认值是false，当结果值为true时，服务方序列化和调用方反序列化字节码时对不上导致异常
    private Boolean hasPrevious;
    private Boolean hasNext;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }
}
