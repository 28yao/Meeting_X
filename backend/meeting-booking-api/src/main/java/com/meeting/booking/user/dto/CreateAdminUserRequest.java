package com.meeting.booking.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 管理员创建用户请求体。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class CreateAdminUserRequest {

    @NotBlank(message = "登录账号不能为空")
    @Size(max = 64, message = "登录账号过长")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度须在 6～64 位")
    private String password;

    @NotBlank(message = "显示名称不能为空")
    @Size(max = 128, message = "显示名称过长")
    private String displayName;

    @NotBlank(message = "角色不能为空")
    private String role;

    private Boolean enabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
