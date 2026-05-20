package com.meeting.booking.notification.dto;

/**
 * 未读通知数量 DTO。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class UnreadCountDto {

    private int count;

    public UnreadCountDto() {
    }

    public UnreadCountDto(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
