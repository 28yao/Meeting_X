package com.meeting.booking.common;

/**
 * 业务错误码常量，与 plan §8.5 及前端 errorMessages 对齐。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class ErrorCodes {

    public static final int INVALID_TIME_RANGE = 40001;
    public static final int CROSS_DAY_NOT_ALLOWED = 40002;
    public static final int TIME_SLOT_NOT_ALIGNED = 40003;
    public static final int PAST_TIME_NOT_ALLOWED = 40004;
    public static final int EXCEED_ADVANCE_LIMIT = 40005;
    public static final int ROOM_SLOT_OCCUPIED = 40901;
    public static final int ROOM_UNDER_MAINTENANCE = 40902;
    public static final int ROOM_HAS_FUTURE_BOOKINGS = 40903;
    public static final int ROOM_NOT_FOUND = 40401;
    public static final int BOOKING_NOT_FOUND = 40402;
    public static final int BOOKING_CANNOT_CANCEL = 40302;
    public static final int BOOKING_ALREADY_CANCELLED = 40303;
    public static final int NOTIFICATION_NOT_FOUND = 40403;
    public static final int USER_NOT_FOUND = 40404;
    public static final int USERNAME_ALREADY_EXISTS = 40904;
    public static final int USER_HAS_BOOKINGS = 40905;
    public static final int USER_CANNOT_DELETE_SELF = 40304;
    public static final int LAST_ADMIN_CANNOT_DELETE = 40305;

    private ErrorCodes() {
    }
}
