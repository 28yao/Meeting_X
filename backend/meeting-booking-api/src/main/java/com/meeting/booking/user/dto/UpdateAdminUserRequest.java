package com.meeting.booking.user.dto;

import javax.validation.constraints.Size;

/**
 * 管理员更新用户请求体。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class UpdateAdminUserRequest {

    @Size(max = 128, message = "显示名称过长")
    private String displayName;

    private String role;

    private Boolean enabled;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
