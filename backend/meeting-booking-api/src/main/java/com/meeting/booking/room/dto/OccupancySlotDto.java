package com.meeting.booking.room.dto;

import java.time.LocalDateTime;

/**
 * 会议室某日占用片段 DTO（他人预约可见主题与组织者）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class OccupancySlotDto {

    private String title;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
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
