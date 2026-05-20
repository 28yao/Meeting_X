package com.meeting.booking.auth.dto;

/**
 * 当前用户信息响应 DTO，用于 /auth/me 与登录返回。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class UserInfoResponse {

    private Long id;

    private String username;

    private String displayName;

    private String role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
