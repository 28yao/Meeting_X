package com.meeting.booking.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meeting.booking.booking.mapper.BookingMapper;
import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import com.meeting.booking.common.PagingDefaults;
import com.meeting.booking.common.dto.PageResult;
import com.meeting.booking.notification.entity.Notification;
import com.meeting.booking.notification.mapper.NotificationMapper;
import com.meeting.booking.user.dto.AdminUserDto;
import com.meeting.booking.user.dto.CreateAdminUserRequest;
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
 * 管理员用户管理业务服务：创建、编辑、删除、重置密码与列表查询。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class AdminUserService {

    private static final int ENABLED = 1;
    private static final int DISABLED = 0;

    private final SysUserMapper sysUserMapper;
    private final BookingMapper bookingMapper;
    private final NotificationMapper notificationMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(SysUserMapper sysUserMapper,
                            BookingMapper bookingMapper,
                            NotificationMapper notificationMapper,
                            PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.bookingMapper = bookingMapper;
        this.notificationMapper = notificationMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询用户列表（按 ID 升序）。
     *
     * @param page 页码，从 1 开始
     * @return 分页结果
     */
    public PageResult<AdminUserDto> listUsers(int page) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .orderByAsc(SysUser::getId);
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        List<AdminUserDto> all = new ArrayList<AdminUserDto>();
        if (users != null) {
            for (SysUser user : users) {
                all.add(toDto(user));
            }
        }
        return PagingDefaults.slice(all, page);
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
        user.setPasswordHash(passwordEncoder.encode(resolvePassword(request.getPassword())));
        user.setDisplayName(resolveDisplayName(username, request.getDisplayName()));
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
     * 将用户密码重置为系统默认密码。
     *
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        SysUser user = requireUser(userId);
        user.setPasswordHash(passwordEncoder.encode(AdminUserDefaults.DEFAULT_PASSWORD));
        sysUserMapper.updateById(user);
    }

    /**
     * 删除用户（物理删除）。不可删除当前登录账号、最后一位启用管理员，或存在预约记录的用户。
     *
     * @param userId           待删除用户 ID
     * @param operatorUserId   当前操作者用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId, Long operatorUserId) {
        if (userId != null && userId.equals(operatorUserId)) {
            throw new BusinessException(ErrorCodes.USER_CANNOT_DELETE_SELF,
                    "不能删除当前登录账号", HttpStatus.BAD_REQUEST.value());
        }
        SysUser user = requireUser(userId);
        if (UserRole.ADMIN.equals(user.getRole())
                && user.getEnabled() != null
                && user.getEnabled() == ENABLED
                && sysUserMapper.countEnabledAdmins() <= 1) {
            throw new BusinessException(ErrorCodes.LAST_ADMIN_CANNOT_DELETE,
                    "不能删除最后一位启用的管理员", HttpStatus.BAD_REQUEST.value());
        }
        if (bookingMapper.countByOrganizerId(userId) > 0) {
            throw new BusinessException(ErrorCodes.USER_HAS_BOOKINGS,
                    "该用户存在预约记录，无法删除", HttpStatus.CONFLICT.value());
        }
        LambdaQueryWrapper<Notification> notificationWrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId);
        notificationMapper.delete(notificationWrapper);
        sysUserMapper.deleteById(userId);
    }

    private String resolvePassword(String password) {
        String trimmed = trim(password);
        if (trimmed == null || trimmed.isEmpty()) {
            return AdminUserDefaults.DEFAULT_PASSWORD;
        }
        return trimmed;
    }

    private String resolveDisplayName(String username, String displayName) {
        String trimmed = trim(displayName);
        if (trimmed == null || trimmed.isEmpty()) {
            return username;
        }
        return trimmed;
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
