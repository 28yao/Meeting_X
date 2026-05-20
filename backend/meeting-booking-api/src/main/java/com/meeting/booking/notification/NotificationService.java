package com.meeting.booking.notification;

import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import com.meeting.booking.notification.dto.NotificationDto;
import com.meeting.booking.notification.dto.UnreadCountDto;
import com.meeting.booking.notification.entity.Notification;
import com.meeting.booking.notification.mapper.NotificationMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 站内通知查询与已读标记服务。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class NotificationService {

    private static final int READ_FLAG_UNREAD = 0;

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    /**
     * 查询当前用户全部通知（按时间降序）。
     *
     * @param userId 用户 ID
     * @return 通知 DTO 列表
     */
    public List<NotificationDto> listByUser(Long userId) {
        List<Notification> rows = notificationMapper.selectByUserId(userId);
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<NotificationDto> result = new ArrayList<NotificationDto>();
        for (Notification row : rows) {
            result.add(toDto(row));
        }
        return result;
    }

    /**
     * 统计当前用户未读通知数量。
     *
     * @param userId 用户 ID
     * @return 未读数量 DTO
     */
    public UnreadCountDto countUnread(Long userId) {
        int count = notificationMapper.countUnreadByUserId(userId);
        return new UnreadCountDto(count);
    }

    /**
     * 将单条通知标为已读。
     *
     * @param notificationId 通知 ID
     * @param userId         用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long notificationId, Long userId) {
        Notification existing = notificationMapper.selectById(notificationId);
        if (existing == null || !userId.equals(existing.getUserId())) {
            throw new BusinessException(ErrorCodes.NOTIFICATION_NOT_FOUND, "通知不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        if (existing.getReadFlag() != null && existing.getReadFlag() != READ_FLAG_UNREAD) {
            return;
        }
        notificationMapper.markReadByIdAndUserId(notificationId, userId);
    }

    /**
     * 将当前用户全部未读通知标为已读。
     *
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long userId) {
        notificationMapper.markAllReadByUserId(userId);
    }

    private NotificationDto toDto(Notification row) {
        NotificationDto dto = new NotificationDto();
        dto.setId(row.getId());
        dto.setType(row.getType());
        dto.setTitle(row.getTitle());
        dto.setContent(row.getContent());
        dto.setRelatedBookingId(row.getRelatedBookingId());
        dto.setRead(row.getReadFlag() != null && row.getReadFlag() != READ_FLAG_UNREAD);
        dto.setCreatedAt(row.getCreatedAt());
        return dto;
    }
}
