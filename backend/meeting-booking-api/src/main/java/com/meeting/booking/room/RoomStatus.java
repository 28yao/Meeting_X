package com.meeting.booking.room;

/**
 * 会议室状态常量。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class RoomStatus {

    public static final String NORMAL = "NORMAL";
    public static final String MAINTENANCE = "MAINTENANCE";

    private RoomStatus() {
    }

    /**
     * 判断是否为合法状态值。
     *
     * @param status 状态字符串
     * @return 合法为 true
     */
    public static boolean isValid(String status) {
        return NORMAL.equals(status) || MAINTENANCE.equals(status);
    }
}
