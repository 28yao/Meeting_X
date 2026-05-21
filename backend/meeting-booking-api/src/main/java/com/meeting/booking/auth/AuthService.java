package com.meeting.booking.auth;

import com.meeting.booking.auth.dto.LoginRequest;
import com.meeting.booking.auth.dto.LoginResponse;
import com.meeting.booking.auth.dto.RegisterRequest;
import com.meeting.booking.auth.dto.UserInfoResponse;
import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import com.meeting.booking.user.entity.SysUser;
import com.meeting.booking.user.mapper.SysUserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证业务服务，负责校验账号密码、签发 JWT 及组装用户信息。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class AuthService {

    private static final int CODE_INVALID_CREDENTIALS = 40101;
    private static final int CODE_ACCOUNT_DISABLED = 40102;

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(SysUserMapper sysUserMapper,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 用户登录：校验账号密码并返回 Token。
     *
     * @param request 登录请求
     * @return 登录响应
     */
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername() == null ? null : request.getUsername().trim();
        String password = request.getPassword() == null ? null : request.getPassword().trim();
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null || user.getPasswordHash() == null
                || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(CODE_INVALID_CREDENTIALS, "账号或密码错误", HttpStatus.UNAUTHORIZED.value());
        }
        if (user.getEnabled() == null || user.getEnabled() != 1) {
            throw new BusinessException(CODE_ACCOUNT_DISABLED, "账号已禁用，请联系管理员", HttpStatus.UNAUTHORIZED.value());
        }
        LoginResponse response = new LoginResponse();
        response.setToken(jwtTokenProvider.createToken(user));
        response.setUser(toUserInfo(user));
        return response;
    }

    /**
     * 用户自助注册，创建 EMPLOYEE 账号并返回 JWT。
     *
     * @param request 注册请求
     * @return 登录响应（含 token）
     */
    public LoginResponse register(RegisterRequest request) {
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCodes.PASSWORD_MISMATCH, "密码与确认密码不一致",
                    HttpStatus.BAD_REQUEST.value());
        }
        String username = request.getUsername().trim();
        if (sysUserMapper.selectByUsername(username) != null) {
            throw new BusinessException(ErrorCodes.USERNAME_ALREADY_EXISTS, "账号已存在",
                    HttpStatus.CONFLICT.value());
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        String displayName = request.getDisplayName();
        user.setDisplayName(displayName != null && !displayName.trim().isEmpty() ? displayName.trim() : username);
        user.setRole("EMPLOYEE");
        user.setEnabled(1);
        sysUserMapper.insert(user);
        LoginResponse response = new LoginResponse();
        response.setToken(jwtTokenProvider.createToken(user));
        response.setUser(toUserInfo(user));
        return response;
    }

    /**
     * 将登录主体转换为用户信息 DTO。
     *
     * @param principal 登录主体
     * @return 用户信息
     */
    public UserInfoResponse toUserInfo(LoginUserPrincipal principal) {
        UserInfoResponse info = new UserInfoResponse();
        info.setId(principal.getUserId());
        info.setUsername(principal.getUsername());
        info.setDisplayName(principal.getDisplayName());
        info.setRole(principal.getRole());
        return info;
    }

    private UserInfoResponse toUserInfo(SysUser user) {
        UserInfoResponse info = new UserInfoResponse();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setDisplayName(user.getDisplayName());
        info.setRole(user.getRole());
        return info;
    }
}
