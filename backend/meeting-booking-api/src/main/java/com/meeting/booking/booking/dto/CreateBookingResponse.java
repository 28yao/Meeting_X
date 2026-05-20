package com.meeting.booking.booking.dto;

import java.time.LocalDateTime;

/**
 * 创建预约成功响应。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class CreateBookingResponse {

    private Long bookingId;
    private Long roomId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
