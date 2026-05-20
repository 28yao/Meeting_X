package com.meeting.booking.auth.dto;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求体，包含账号与密码。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class LoginRequest {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

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
}
