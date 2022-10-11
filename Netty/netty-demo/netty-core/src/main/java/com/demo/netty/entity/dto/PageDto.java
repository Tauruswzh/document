package com.demo.netty.entity.dto;


import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件名: PageDto.java
 * 描述: 分页查询参数
 */
public class PageDto<T> implements Serializable {

    /**
     * 页号
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private int pageNum;

    /**
     * 页大小
     */
    @Min(value = 1, message = "页大小必须大于等于1")
    private int pageSize;

    /**
     * 查询条件
     */
    @Valid
    @NotNull
    private T data;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
