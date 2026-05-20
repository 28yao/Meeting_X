package com.meeting.booking.booking;

import com.meeting.booking.auth.LoginUserPrincipal;
import com.meeting.booking.booking.dto.CreateBookingRequest;
import com.meeting.booking.booking.dto.CreateBookingResponse;
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
 * 预约创建业务服务，含时间校验、冲突检测与并发控制。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class BookingService {

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String ROOM_STATUS_NORMAL = "NORMAL";

    private final BookingMapper bookingMapper;
    private final MeetingRoomMapper meetingRoomMapper;
    private final TimeValidator timeValidator;
    private final BookingConflictChecker conflictChecker;
    private final NotificationPublisher notificationPublisher;

    public BookingService(BookingMapper bookingMapper,
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
     * 创建预约。
     *
     * @param request   请求体
     * @param principal 当前登录用户
     * @return 创建结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CreateBookingResponse create(CreateBookingRequest request, LoginUserPrincipal principal) {
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();
        timeValidator.validate(start, end);

        MeetingRoom room = meetingRoomMapper.selectById(request.getRoomId());
        if (room == null || room.getDeleted() != null && room.getDeleted() == 1) {
            throw new BusinessException(ErrorCodes.ROOM_NOT_FOUND, "会议室不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        if (!ROOM_STATUS_NORMAL.equals(room.getStatus())) {
            throw new BusinessException(ErrorCodes.ROOM_UNDER_MAINTENANCE,
                    "该会议室维护中，暂不可预约", HttpStatus.CONFLICT.value());
        }
        if (conflictChecker.hasOverlap(request.getRoomId(), start, end)) {
            throw new BusinessException(ErrorCodes.ROOM_SLOT_OCCUPIED,
                    "该时段已被预约，请更换时间或会议室", HttpStatus.CONFLICT.value());
        }

        Booking booking = new Booking();
        booking.setRoomId(request.getRoomId());
        booking.setOrganizerId(principal.getUserId());
        booking.setTitle(request.getTitle().trim());
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setStatus(STATUS_CONFIRMED);
        bookingMapper.insert(booking);

        if (conflictChecker.countOverlap(request.getRoomId(), start, end) > 1) {
            throw new BusinessException(ErrorCodes.ROOM_SLOT_OCCUPIED,
                    "该时段已被他人抢先预约，请重新选择", HttpStatus.CONFLICT.value());
        }

        notificationPublisher.publishBookingSuccess(
                principal.getUserId(),
                booking.getId(),
                booking.getTitle(),
                room.getName(),
                booking.getStartTime(),
                booking.getEndTime());

        CreateBookingResponse response = new CreateBookingResponse();
        response.setBookingId(booking.getId());
        response.setRoomId(booking.getRoomId());
        response.setTitle(booking.getTitle());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        return response;
    }
}
