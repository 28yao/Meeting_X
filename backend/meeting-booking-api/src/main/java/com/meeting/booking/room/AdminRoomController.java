package com.meeting.booking.room;

import com.meeting.booking.common.ApiResponse;
import com.meeting.booking.room.dto.CreateAdminRoomRequest;
import com.meeting.booking.room.dto.MeetingRoomDto;
import com.meeting.booking.room.dto.UpdateAdminRoomRequest;
import com.meeting.booking.room.dto.UpdateRoomStatusRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meeting.booking.common.dto.PageResult;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * 管理员会议室管理 REST 接口（仅 ADMIN 角色可访问）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
@RequestMapping("/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

    private final MeetingRoomAdminService meetingRoomAdminService;

    public AdminRoomController(MeetingRoomAdminService meetingRoomAdminService) {
        this.meetingRoomAdminService = meetingRoomAdminService;
    }

    /**
     * 分页查询会议室列表。
     *
     * @param page 页码，默认 1
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResult<MeetingRoomDto>> list(
            @RequestParam(defaultValue = "1") int page) {
        return ApiResponse.success(meetingRoomAdminService.listRooms(page));
    }

    /**
     * 创建会议室。
     *
     * @param request 创建请求
     * @return 新会议室信息
     */
    @PostMapping
    public ApiResponse<MeetingRoomDto> create(@Valid @RequestBody CreateAdminRoomRequest request) {
        return ApiResponse.success(meetingRoomAdminService.createRoom(request));
    }

    /**
     * 更新会议室信息。
     *
     * @param id      会议室 ID
     * @param request 更新请求
     * @return 更新后信息
     */
    @PutMapping("/{id}")
    public ApiResponse<MeetingRoomDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateAdminRoomRequest request) {
        return ApiResponse.success(meetingRoomAdminService.updateRoom(id, request));
    }

    /**
     * 更新会议室状态。
     *
     * @param id      会议室 ID
     * @param request 状态请求
     * @return 更新后信息
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<MeetingRoomDto> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateRoomStatusRequest request) {
        return ApiResponse.success(meetingRoomAdminService.updateStatus(id, request.getStatus()));
    }

    /**
     * 逻辑删除会议室。
     *
     * @param id 会议室 ID
     * @return 空成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        meetingRoomAdminService.deleteRoom(id);
        return ApiResponse.success(null);
    }
}
