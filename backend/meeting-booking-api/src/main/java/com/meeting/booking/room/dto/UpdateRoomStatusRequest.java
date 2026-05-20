package com.meeting.booking.room.dto;

import javax.validation.constraints.NotBlank;

/**
 * 管理员更新会议室状态请求体。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class UpdateRoomStatusRequest {

    @NotBlank(message = "状态不能为空")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
