package com.meeting.booking.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import com.meeting.booking.user.dto.AdminUserDto;
import com.meeting.booking.user.dto.CreateAdminUserRequest;
import com.meeting.booking.user.dto.ResetPasswordRequest;
import com.meeting.booking.user.dto.UpdateAdminUserRequest;
import com.meeting.booking.user.entity.SysUser;
import com.meeting.booking.user.mapper.SysUserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 管理员用户管理业务服务：创建、编辑、重置密码与列表查询。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class AdminUserService {

    private static final int ENABLED = 1;
    private static final int DISABLED = 0;

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 查询全部用户（按 ID 升序）。
     *
     * @return 用户 DTO 列表
     */
    public List<AdminUserDto> listUsers() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .orderByAsc(SysUser::getId);
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        List<AdminUserDto> result = new ArrayList<AdminUserDto>();
        for (SysUser user : users) {
            result.add(toDto(user));
        }
        return result;
    }

    /**
     * 创建新用户（BCrypt 加密密码）。
     *
     * @param request 创建请求
     * @return 新用户 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public AdminUserDto createUser(CreateAdminUserRequest request) {
        String username = trim(request.getUsername());
        validateRole(request.getRole());
        if (sysUserMapper.selectByUsername(username) != null) {
            throw new BusinessException(ErrorCodes.USERNAME_ALREADY_EXISTS,
                    "登录账号已存在", HttpStatus.CONFLICT.value());
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(trim(request.getPassword())));
        user.setDisplayName(trim(request.getDisplayName()));
        user.setRole(request.getRole().trim());
        user.setEnabled(resolveEnabled(request.getEnabled()));
        sysUserMapper.insert(user);
        return toDto(user);
    }

    /**
     * 更新用户信息（不含密码与账号）。
     *
     * @param userId  用户 ID
     * @param request 更新请求
     * @return 更新后 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public AdminUserDto updateUser(Long userId, UpdateAdminUserRequest request) {
        SysUser user = requireUser(userId);
        if (request.getDisplayName() != null) {
            user.setDisplayName(trim(request.getDisplayName()));
        }
        if (request.getRole() != null) {
            validateRole(request.getRole());
            user.setRole(request.getRole().trim());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled() ? ENABLED : DISABLED);
        }
        sysUserMapper.updateById(user);
        return toDto(user);
    }

    /**
     * 重置用户密码。
     *
     * @param userId  用户 ID
     * @param request 新密码请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, ResetPasswordRequest request) {
        SysUser user = requireUser(userId);
        user.setPasswordHash(passwordEncoder.encode(trim(request.getPassword())));
        sysUserMapper.updateById(user);
    }

    private SysUser requireUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodes.USER_NOT_FOUND, "用户不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        return user;
    }

    private void validateRole(String role) {
        if (role == null || !UserRole.isValid(role.trim())) {
            throw new BusinessException(40006, "角色必须为 EMPLOYEE 或 ADMIN",
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private int resolveEnabled(Boolean enabled) {
        if (enabled == null || enabled) {
            return ENABLED;
        }
        return DISABLED;
    }

    private AdminUserDto toDto(SysUser user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled() != null && user.getEnabled() == ENABLED);
        return dto;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
