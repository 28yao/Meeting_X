package com.meeting.booking.booking;

import java.time.LocalDateTime;

/**
 * 预约展示状态推导与排序辅助（未开始/进行中/已结束/已取消）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class BookingDisplayStatus {

    public static final String UPCOMING = "UPCOMING";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String ENDED = "ENDED";
    public static final String CANCELLED = "CANCELLED";

    private static final String DB_CONFIRMED = "CONFIRMED";
    private static final String DB_CANCELLED = "CANCELLED";

    private BookingDisplayStatus() {
    }

    /**
     * 根据库状态与时段推导展示状态。
     *
     * @param dbStatus  库中状态
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param now       当前时间
     * @return 展示状态编码
     */
    public static String derive(String dbStatus, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime now) {
        if (DB_CANCELLED.equals(dbStatus)) {
            return CANCELLED;
        }
        if (!DB_CONFIRMED.equals(dbStatus)) {
            return ENDED;
        }
        if (now.isBefore(startTime)) {
            return UPCOMING;
        }
        if (now.isBefore(endTime)) {
            return IN_PROGRESS;
        }
        return ENDED;
    }

    /**
     * 列表排序主键：未开始/进行中优先，其次已结束/已取消。
     *
     * @param displayStatus 展示状态
     * @return 排序权重（越小越靠前）
     */
    public static int groupOrder(String displayStatus) {
        if (UPCOMING.equals(displayStatus)) {
            return 0;
        }
        if (IN_PROGRESS.equals(displayStatus)) {
            return 1;
        }
        if (ENDED.equals(displayStatus)) {
            return 2;
        }
        return 3;
    }

    /**
     * 是否允许用户取消（未开始且仍为 CONFIRMED）。
     *
     * @param dbStatus      库中状态
     * @param displayStatus 展示状态
     * @return 可取消为 true
     */
    public static boolean cancellable(String dbStatus, String displayStatus) {
        return DB_CONFIRMED.equals(dbStatus) && UPCOMING.equals(displayStatus);
    }
}
