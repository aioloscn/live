package com.aiolos.live.common.wrapper;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private long total;
    private long size;
    private long current;
    private List<T> records;
}
