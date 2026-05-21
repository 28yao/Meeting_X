package com.meeting.booking.common.dto;

import java.util.List;

/**
 * 分页结果封装，供列表类接口统一返回结构。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class PageResult<T> {

    private List<T> items;
    private int page;
    private int pageSize;
    private long total;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
