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
    public static final int ROOM_NOT_FOUND = 40401;

    private ErrorCodes() {
    }
}
