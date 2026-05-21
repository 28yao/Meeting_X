package com.meeting.booking.booking;

import com.meeting.booking.booking.dto.AdminBookingItemDto;
import com.meeting.booking.common.dto.PageResult;
import com.meeting.booking.booking.dto.UpdateAdminBookingRequest;
import com.meeting.booking.common.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理员预约管理 REST 接口（仅 ADMIN 角色可访问）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
@RequestMapping("/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final AdminBookingQueryService adminBookingQueryService;
    private final AdminBookingUpdateService adminBookingUpdateService;
    private final AdminBookingCancelService adminBookingCancelService;

    public AdminBookingController(AdminBookingQueryService adminBookingQueryService,
                                  AdminBookingUpdateService adminBookingUpdateService,
                                  AdminBookingCancelService adminBookingCancelService) {
        this.adminBookingQueryService = adminBookingQueryService;
        this.adminBookingUpdateService = adminBookingUpdateService;
        this.adminBookingCancelService = adminBookingCancelService;
    }

    /**
     * 分页查询全公司预约列表。
     *
     * @param page 页码，默认 1
     * @return 分页列表
     */
    @GetMapping
    public ApiResponse<PageResult<AdminBookingItemDto>> list(
            @RequestParam(defaultValue = "1") int page) {
        return ApiResponse.success(adminBookingQueryService.listAll(page));
    }

    /**
     * 修改预约时间与会议室。
     *
     * @param id      预约 ID
     * @param request 更新请求
     * @return 更新后信息
     */
    @PutMapping("/{id}")
    public ApiResponse<AdminBookingItemDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateAdminBookingRequest request) {
        return ApiResponse.success(adminBookingUpdateService.update(id, request));
    }

    /**
     * 取消预约。
     *
     * @param id 预约 ID
     * @return 空成功响应
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable("id") Long id) {
        adminBookingCancelService.cancel(id);
        return ApiResponse.success(null);
    }
}
