package com.meeting.booking.booking;

import com.meeting.booking.booking.dto.AdminBookingItemDto;
import com.meeting.booking.booking.dto.UpdateAdminBookingRequest;
import com.meeting.booking.booking.entity.Booking;
import com.meeting.booking.booking.mapper.BookingMapper;
import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import com.meeting.booking.notification.NotificationPublisher;
import com.meeting.booking.room.RoomStatus;
import com.meeting.booking.room.entity.MeetingRoom;
import com.meeting.booking.room.mapper.MeetingRoomMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员修改预约业务服务（改时间、换会议室）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class AdminBookingUpdateService {

    private final BookingMapper bookingMapper;
    private final MeetingRoomMapper meetingRoomMapper;
    private final TimeValidator timeValidator;
    private final BookingConflictChecker conflictChecker;
    private final NotificationPublisher notificationPublisher;

    public AdminBookingUpdateService(BookingMapper bookingMapper,
                                     MeetingRoomMapper meetingRoomMapper,
                                     TimeValidator timeValidator,
                                     BookingConflictChecker conflictChecker,
                                     NotificationPublisher notificationPublisher) {
        this.bookingMapper = bookingMapper;
        this.meetingRoomMapper = meetingRoomMapper;
        this.timeValidator = timeValidator;
        this.conflictChecker = conflictChecker;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * 更新预约的会议室与时段。
     *
     * @param bookingId 预约 ID
     * @param request   更新请求
     * @return 更新后的列表项 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public AdminBookingItemDto update(Long bookingId, UpdateAdminBookingRequest request) {
        Booking booking = requireModifiableBooking(bookingId);
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();
        timeValidator.validate(start, end);

        MeetingRoom room = meetingRoomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException(ErrorCodes.ROOM_NOT_FOUND, "会议室不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        if (!RoomStatus.NORMAL.equals(room.getStatus())) {
            throw new BusinessException(ErrorCodes.ROOM_UNDER_MAINTENANCE,
                    "该会议室维护中，暂不可预约", HttpStatus.CONFLICT.value());
        }
        if (conflictChecker.hasOverlapExcluding(bookingId, request.getRoomId(), start, end)) {
            throw new BusinessException(ErrorCodes.ROOM_SLOT_OCCUPIED,
                    "该时段已被预约，请更换时间或会议室", HttpStatus.CONFLICT.value());
        }

        int updated = bookingMapper.updateTimeAndRoom(bookingId, request.getRoomId(), start, end);
        if (updated == 0) {
            throw new BusinessException(ErrorCodes.BOOKING_CANNOT_CANCEL,
                    "修改失败，请刷新后重试", HttpStatus.FORBIDDEN.value());
        }

        notificationPublisher.publishBookingUpdatedByAdmin(
                booking.getOrganizerId(),
                bookingId,
                booking.getTitle(),
                room.getName(),
                start,
                end);

        AdminBookingItemDto dto = new AdminBookingItemDto();
        dto.setBookingId(bookingId);
        dto.setTitle(booking.getTitle());
        dto.setRoomId(request.getRoomId());
        dto.setRoomName(room.getName());
        dto.setOrganizerId(booking.getOrganizerId());
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setStatus("CONFIRMED");
        dto.setDisplayStatus(BookingDisplayStatus.UPCOMING);
        dto.setEditable(true);
        dto.setCancellable(true);
        return dto;
    }

    private Booking requireModifiableBooking(Long bookingId) {
        Booking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException(ErrorCodes.BOOKING_NOT_FOUND, "预约不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        LocalDateTime now = LocalDateTime.now();
        String displayStatus = BookingDisplayStatus.derive(
                booking.getStatus(), booking.getStartTime(), booking.getEndTime(), now);
        if (!BookingDisplayStatus.cancellable(booking.getStatus(), displayStatus)) {
            throw new BusinessException(ErrorCodes.BOOKING_CANNOT_CANCEL,
                    "已开始或已结束的预约不可修改", HttpStatus.FORBIDDEN.value());
        }
        return booking;
    }
}
