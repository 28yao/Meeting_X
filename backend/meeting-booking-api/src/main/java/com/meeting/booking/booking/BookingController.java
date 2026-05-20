package com.meeting.booking.booking;

import com.meeting.booking.auth.LoginUserPrincipal;
import com.meeting.booking.booking.dto.CreateBookingRequest;
import com.meeting.booking.booking.dto.CreateBookingResponse;
import com.meeting.booking.booking.dto.MyBookingItemDto;
import com.meeting.booking.booking.dto.PageResult;
import com.meeting.booking.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 员工端预约接口。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingQueryService bookingQueryService;
    private final BookingCancelService bookingCancelService;

    public BookingController(BookingService bookingService,
                             BookingQueryService bookingQueryService,
                             BookingCancelService bookingCancelService) {
        this.bookingService = bookingService;
        this.bookingQueryService = bookingQueryService;
        this.bookingCancelService = bookingCancelService;
    }

    /**
     * 创建会议室预约。
     *
     * @param request   预约请求
     * @param principal 当前用户
     * @return 预约结果
     */
    @PostMapping
    public ApiResponse<CreateBookingResponse> create(
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        CreateBookingResponse response = bookingService.create(request, principal);
        return ApiResponse.success(response);
    }

    /**
     * 分页查询我的预约。
     *
     * @param page      页码，默认 1
     * @param principal 当前用户
     * @return 分页列表
     */
    @GetMapping("/mine")
    public ApiResponse<PageResult<MyBookingItemDto>> listMine(
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        PageResult<MyBookingItemDto> result = bookingQueryService.listMine(principal.getUserId(), page);
        return ApiResponse.success(result);
    }

    /**
     * 取消本人未开始的预约。
     *
     * @param id        预约 ID
     * @param principal 当前用户
     * @return 空数据成功响应
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        bookingCancelService.cancel(id, principal);
        return ApiResponse.success(null);
    }
}
