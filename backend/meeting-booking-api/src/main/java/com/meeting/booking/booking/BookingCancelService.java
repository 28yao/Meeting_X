package com.meeting.booking.booking;

import com.meeting.booking.auth.LoginUserPrincipal;
import com.meeting.booking.booking.entity.Booking;
import com.meeting.booking.booking.mapper.BookingMapper;
import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 员工取消本人预约业务服务。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class BookingCancelService {

    private static final String STATUS_CANCELLED = "CANCELLED";

    private final BookingMapper bookingMapper;

    public BookingCancelService(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    /**
     * 取消预约（仅组织者、未开始且 CONFIRMED）。
     *
     * @param bookingId 预约 ID
     * @param principal 当前用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long bookingId, LoginUserPrincipal principal) {
        Booking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException(ErrorCodes.BOOKING_NOT_FOUND, "预约不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        if (!principal.getUserId().equals(booking.getOrganizerId())) {
            throw new BusinessException(40300, "无权限操作该预约",
                    HttpStatus.FORBIDDEN.value());
        }
        if (STATUS_CANCELLED.equals(booking.getStatus())) {
            throw new BusinessException(ErrorCodes.BOOKING_ALREADY_CANCELLED,
                    "该预约已取消", HttpStatus.FORBIDDEN.value());
        }
        LocalDateTime now = LocalDateTime.now();
        String displayStatus = BookingDisplayStatus.derive(
                booking.getStatus(), booking.getStartTime(), booking.getEndTime(), now);
        if (!BookingDisplayStatus.cancellable(booking.getStatus(), displayStatus)) {
            throw new BusinessException(ErrorCodes.BOOKING_CANNOT_CANCEL,
                    "已开始或已结束的预约不可取消", HttpStatus.FORBIDDEN.value());
        }
        int updated = bookingMapper.updateStatusToCancelled(bookingId, principal.getUserId());
        if (updated == 0) {
            throw new BusinessException(ErrorCodes.BOOKING_CANNOT_CANCEL,
                    "取消失败，请刷新后重试", HttpStatus.FORBIDDEN.value());
        }
    }
}
