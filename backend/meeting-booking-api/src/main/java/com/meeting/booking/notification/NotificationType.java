package com.meeting.booking.notification;

/**
 * 站内通知类型常量。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class NotificationType {

    public static final String BOOKING_SUCCESS = "BOOKING_SUCCESS";
    public static final String BOOKING_CANCELLED = "BOOKING_CANCELLED";
    public static final String BOOKING_ADMIN_CANCELLED = "BOOKING_ADMIN_CANCELLED";
    public static final String BOOKING_ADMIN_UPDATED = "BOOKING_ADMIN_UPDATED";

    private NotificationType() {
    }
}
