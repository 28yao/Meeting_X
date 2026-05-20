package com.meeting.booking.booking.dto;

import java.time.LocalDateTime;

/**
 * 管理员预约列表查询行（含会议室与组织者显示名）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class AdminBookingRow {

    private Long id;
    private Long roomId;
    private Long organizerId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String roomName;
    private String organizerDisplayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getOrganizerDisplayName() {
        return organizerDisplayName;
    }

    public void setOrganizerDisplayName(String organizerDisplayName) {
        this.organizerDisplayName = organizerDisplayName;
    }
}
