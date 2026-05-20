package com.meeting.booking.notification;

import com.meeting.booking.notification.entity.Notification;
import com.meeting.booking.notification.mapper.NotificationMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 站内通知发布器：在预约创建、取消等事件后写入通知。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Component
public class NotificationPublisher {

    private static final int READ_FLAG_UNREAD = 0;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final NotificationMapper notificationMapper;

    public NotificationPublisher(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    /**
     * 预约成功时通知组织者。
     *
     * @param userId    组织者 ID
     * @param bookingId 预约 ID
     * @param title     会议主题
     * @param roomName  会议室名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void publishBookingSuccess(Long userId, Long bookingId, String title, String roomName,
                                      LocalDateTime startTime, LocalDateTime endTime) {
        String timeRange = formatRange(startTime, endTime);
        String content = String.format("您已成功预约「%s」（%s），时段：%s。", title, roomName, timeRange);
        insert(userId, NotificationType.BOOKING_SUCCESS, "预约成功", content, bookingId);
    }

    /**
     * 用户自行取消预约时通知组织者。
     *
     * @param userId    组织者 ID
     * @param bookingId 预约 ID
     * @param title     会议主题
     * @param roomName  会议室名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void publishBookingCancelledBySelf(Long userId, Long bookingId, String title, String roomName,
                                              LocalDateTime startTime, LocalDateTime endTime) {
        String timeRange = formatRange(startTime, endTime);
        String content = String.format("您已取消预约「%s」（%s），原时段：%s。", title, roomName, timeRange);
        insert(userId, NotificationType.BOOKING_CANCELLED, "预约已取消", content, bookingId);
    }

    /**
     * 管理员取消他人预约时通知原组织者（模块 8 调用）。
     *
     * @param organizerId 原组织者 ID
     * @param bookingId   预约 ID
     * @param title       会议主题
     * @param roomName    会议室名称
     * @param startTime   开始时间
     * @param endTime     结束时间
     */
    public void publishBookingCancelledByAdmin(Long organizerId, Long bookingId, String title,
                                               String roomName, LocalDateTime startTime,
                                               LocalDateTime endTime) {
        String timeRange = formatRange(startTime, endTime);
        String content = String.format("管理员已取消您的预约「%s」（%s），原时段：%s。",
                title, roomName, timeRange);
        insert(organizerId, NotificationType.BOOKING_ADMIN_CANCELLED, "预约已被管理员取消",
                content, bookingId);
    }

    /**
     * 管理员修改他人预约时通知原组织者（模块 8 调用）。
     *
     * @param organizerId 原组织者 ID
     * @param bookingId   预约 ID
     * @param title       会议主题
     * @param roomName    会议室名称
     * @param startTime   新的开始时间
     * @param endTime     新的结束时间
     */
    public void publishBookingUpdatedByAdmin(Long organizerId, Long bookingId, String title,
                                             String roomName, LocalDateTime startTime,
                                             LocalDateTime endTime) {
        String timeRange = formatRange(startTime, endTime);
        String content = String.format("管理员已修改您的预约「%s」，会议室：%s，新时段：%s。",
                title, roomName, timeRange);
        insert(organizerId, NotificationType.BOOKING_ADMIN_UPDATED, "预约已被管理员修改",
                content, bookingId);
    }

    private void insert(Long userId, String type, String title, String content, Long bookingId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedBookingId(bookingId);
        notification.setReadFlag(READ_FLAG_UNREAD);
        notificationMapper.insert(notification);
    }

    private String formatRange(LocalDateTime start, LocalDateTime end) {
        return TIME_FMT.format(start) + " ~ " + TIME_FMT.format(end);
    }
}
