package com.meeting.booking.user;

import com.meeting.booking.auth.LoginUserPrincipal;
import com.meeting.booking.common.ApiResponse;
import com.meeting.booking.user.dto.AdminUserDto;
import com.meeting.booking.user.dto.CreateAdminUserRequest;
import com.meeting.booking.user.dto.UpdateAdminUserRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理员用户管理 REST 接口（仅 ADMIN 角色可访问）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * 查询用户列表。
     *
     * @return 用户列表
     */
    @GetMapping
    public ApiResponse<List<AdminUserDto>> list() {
        return ApiResponse.success(adminUserService.listUsers());
    }

    /**
     * 创建用户。
     *
     * @param request 创建请求
     * @return 新用户信息
     */
    @PostMapping
    public ApiResponse<AdminUserDto> create(@Valid @RequestBody CreateAdminUserRequest request) {
        return ApiResponse.success(adminUserService.createUser(request));
    }

    /**
     * 更新用户信息。
     *
     * @param id      用户 ID
     * @param request 更新请求
     * @return 更新后用户信息
     */
    @PutMapping("/{id}")
    public ApiResponse<AdminUserDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateAdminUserRequest request) {
        return ApiResponse.success(adminUserService.updateUser(id, request));
    }

    /**
     * 将用户密码重置为系统默认密码（123456）。
     *
     * @param id 用户 ID
     * @return 空成功响应
     */
    @PostMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable("id") Long id) {
        adminUserService.resetPassword(id);
        return ApiResponse.success(null);
    }

    /**
     * 删除用户。
     *
     * @param id        用户 ID
     * @param principal 当前登录管理员
     * @return 空成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        adminUserService.deleteUser(id, principal.getUserId());
        return ApiResponse.success(null);
    }
}
