package com.meeting.booking.auth;

/**
 * 当前登录用户主体，存入 SecurityContext 供 Controller 与权限判断使用。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class LoginUserPrincipal {

    private Long userId;

    private String username;

    private String displayName;

    private String role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
