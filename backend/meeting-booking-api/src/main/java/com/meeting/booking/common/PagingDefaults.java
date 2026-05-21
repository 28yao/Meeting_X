package com.meeting.booking.common;

import com.meeting.booking.common.dto.PageResult;

import java.util.Collections;
import java.util.List;

/**
 * 分页默认参数与内存列表切片工具（与一期预约列表每页 20 条一致）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class PagingDefaults {

    /** 默认每页条数 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    private PagingDefaults() {
    }

    /**
     * 将页码规范为从 1 开始的有效值。
     *
     * @param page 请求页码
     * @return 不小于 1 的页码
     */
    public static int safePage(int page) {
        return page < 1 ? 1 : page;
    }

    /**
     * 对已排序的全量列表做内存分页切片。
     *
     * @param all  全量数据
     * @param page 请求页码
     * @param <T>  元素类型
     * @return 分页结果
     */
    public static <T> PageResult<T> slice(List<T> all, int page) {
        int safePage = safePage(page);
        PageResult<T> result = new PageResult<T>();
        result.setPage(safePage);
        result.setPageSize(DEFAULT_PAGE_SIZE);
        if (all == null || all.isEmpty()) {
            result.setItems(Collections.<T>emptyList());
            result.setTotal(0L);
            return result;
        }
        result.setTotal(all.size());
        int from = (safePage - 1) * DEFAULT_PAGE_SIZE;
        if (from >= all.size()) {
            result.setItems(Collections.<T>emptyList());
            return result;
        }
        int to = from + DEFAULT_PAGE_SIZE;
        if (to > all.size()) {
            to = all.size();
        }
        result.setItems(all.subList(from, to));
        return result;
    }
}
