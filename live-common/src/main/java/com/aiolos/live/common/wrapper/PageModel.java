package com.aiolos.live.common.wrapper;

import java.io.Serializable;
import java.util.function.Function;

public class PageModel<T> implements Serializable {
    
    private Page<?> page;
    private T data;
    
    public PageModel() {}

    public PageModel(long current, long size) {
        this.page = Page.of(current, size);
    }
    
    public long getCurrent() {
        return page.getCurrent();
    }

    public void setCurrent(long current) {
        if (page == null) {
            page = new Page<>();
        }
        page.setCurrent(current);
    }
    
    public long getSize() {
        return page.getSize();
    }
    
    public void setSize(long size) {
        if (page == null) {
            page = new Page<>();
        }
        page.setSize(size);
    }
    
    public <R> Page<R> getPage(Class<R> clazz) {
        Page<R> p = (Page<R>) page;
        p.setOptimizeCountSql(true);
        p.setSearchCount(true);
        p.setOptimizeJoinOfCountSql(true);
        return p;
    }
    
    public void setPage(Page<?> page) {
        this.page = page;
    }

    public T getData() {
        return this.data;
    }
    
    public void setData(T data) {
        this.data = data;
    }

    public <R> PageModel<R> convert(Function<T, R> converter) {
        PageModel<R> newModel =  new PageModel<>(this.getCurrent(), this.getSize());
        newModel.page = this.page;
        if (this.data != null) {
            newModel.data = converter.apply(this.data);
        }
        return newModel;
    }
}
