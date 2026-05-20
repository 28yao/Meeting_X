package com.meeting.booking.booking;

import com.meeting.booking.booking.entity.Booking;
import com.meeting.booking.booking.mapper.BookingMapper;
import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import com.meeting.booking.notification.NotificationPublisher;
import com.meeting.booking.room.entity.MeetingRoom;
import com.meeting.booking.room.mapper.MeetingRoomMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员取消任意预约业务服务。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class AdminBookingCancelService {

    private final BookingMapper bookingMapper;
    private final MeetingRoomMapper meetingRoomMapper;
    private final NotificationPublisher notificationPublisher;

    public AdminBookingCancelService(BookingMapper bookingMapper,
                                     MeetingRoomMapper meetingRoomMapper,
                                     NotificationPublisher notificationPublisher) {
        this.bookingMapper = bookingMapper;
        this.meetingRoomMapper = meetingRoomMapper;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * 取消预约（仅未开始且 CONFIRMED），并通知原组织者。
     *
     * @param bookingId 预约 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long bookingId) {
        Booking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException(ErrorCodes.BOOKING_NOT_FOUND, "预约不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        if ("CANCELLED".equals(booking.getStatus())) {
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

        int updated = bookingMapper.updateStatusToCancelledByAdmin(bookingId);
        if (updated == 0) {
            throw new BusinessException(ErrorCodes.BOOKING_CANNOT_CANCEL,
                    "取消失败，请刷新后重试", HttpStatus.FORBIDDEN.value());
        }

        String roomName = resolveRoomName(booking.getRoomId());
        notificationPublisher.publishBookingCancelledByAdmin(
                booking.getOrganizerId(),
                bookingId,
                booking.getTitle(),
                roomName,
                booking.getStartTime(),
                booking.getEndTime());
    }

    private String resolveRoomName(Long roomId) {
        MeetingRoom room = meetingRoomMapper.selectById(roomId);
        return room != null ? room.getName() : "会议室";
    }
}
