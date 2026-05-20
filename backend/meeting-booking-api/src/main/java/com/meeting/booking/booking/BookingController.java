package com.meeting.booking.booking;

import com.meeting.booking.auth.LoginUserPrincipal;
import com.meeting.booking.booking.dto.CreateBookingRequest;
import com.meeting.booking.booking.dto.CreateBookingResponse;
import com.meeting.booking.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
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
}
