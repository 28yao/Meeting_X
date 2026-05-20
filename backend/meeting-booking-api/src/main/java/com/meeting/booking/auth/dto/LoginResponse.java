package com.meeting.booking.auth.dto;

/**
 * 登录成功响应，返回 JWT 与当前用户摘要信息。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class LoginResponse {

    private String token;

    private UserInfoResponse user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfoResponse getUser() {
        return user;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }
}
