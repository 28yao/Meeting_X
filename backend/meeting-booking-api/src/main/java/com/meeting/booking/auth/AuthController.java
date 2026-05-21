package com.meeting.booking.auth;

import com.meeting.booking.auth.dto.LoginRequest;
import com.meeting.booking.auth.dto.LoginResponse;
import com.meeting.booking.auth.dto.RegisterRequest;
import com.meeting.booking.auth.dto.UserInfoResponse;
import com.meeting.booking.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证接口：登录与获取当前用户信息。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录，校验账号密码后返回 JWT。
     *
     * @param request 登录请求体
     * @return 含 token 与用户信息
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    /**
     * 用户自助注册，创建 EMPLOYEE 账号并返回 JWT。
     *
     * @param request 注册请求体
     * @return 含 token 与用户信息
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    /**
     * 获取当前登录用户信息。
     *
     * @param principal 当前登录主体
     * @return 用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> me(@AuthenticationPrincipal LoginUserPrincipal principal) {
        return ApiResponse.success(authService.toUserInfo(principal));
    }
}
