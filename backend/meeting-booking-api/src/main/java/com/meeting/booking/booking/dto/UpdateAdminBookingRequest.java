package com.meeting.booking.booking.dto;

import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 管理员修改预约请求体（时间、会议室）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class UpdateAdminBookingRequest {

    @NotNull(message = "会议室不能为空")
    private Long roomId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
