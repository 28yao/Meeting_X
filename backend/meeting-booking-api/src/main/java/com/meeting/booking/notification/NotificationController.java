package com.meeting.booking.notification;

import com.meeting.booking.auth.LoginUserPrincipal;
import com.meeting.booking.common.ApiResponse;
import com.meeting.booking.common.dto.PageResult;
import com.meeting.booking.notification.dto.NotificationDto;
import com.meeting.booking.notification.dto.UnreadCountDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站内通知 REST 接口。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 分页查询当前用户通知列表。
     *
     * @param page      页码，默认 1
     * @param principal 当前用户
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResult<NotificationDto>> list(
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        PageResult<NotificationDto> result = notificationService.listByUser(principal.getUserId(), page);
        return ApiResponse.success(result);
    }

    /**
     * 查询当前用户未读通知数量。
     *
     * @param principal 当前用户
     * @return 未读数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountDto> unreadCount(
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        UnreadCountDto dto = notificationService.countUnread(principal.getUserId());
        return ApiResponse.success(dto);
    }

    /**
     * 将单条通知标为已读。
     *
     * @param id        通知 ID
     * @param principal 当前用户
     * @return 成功响应
     */
    @PostMapping("/{id}/read")
    public ApiResponse<Void> markRead(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        notificationService.markRead(id, principal.getUserId());
        return ApiResponse.success(null);
    }

    /**
     * 将当前用户全部通知标为已读。
     *
     * @param principal 当前用户
     * @return 成功响应
     */
    @PostMapping("/read-all")
    public ApiResponse<Void> markAllRead(
            @AuthenticationPrincipal LoginUserPrincipal principal) {
        notificationService.markAllRead(principal.getUserId());
        return ApiResponse.success(null);
    }
}
