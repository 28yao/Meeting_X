package com.meeting.booking.room;

import com.meeting.booking.common.ApiResponse;
import com.meeting.booking.room.dto.MeetingRoomDto;
import com.meeting.booking.room.dto.OccupancySlotDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室查询接口（员工端空闲列表与占用片段）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomAvailabilityService roomAvailabilityService;
    private final RoomOccupancyService roomOccupancyService;
    private final MeetingRoomAdminService meetingRoomAdminService;

    public RoomController(RoomAvailabilityService roomAvailabilityService,
                          RoomOccupancyService roomOccupancyService,
                          MeetingRoomAdminService meetingRoomAdminService) {
        this.roomAvailabilityService = roomAvailabilityService;
        this.roomOccupancyService = roomOccupancyService;
        this.meetingRoomAdminService = meetingRoomAdminService;
    }

    /**
     * 查询全部会议室列表（仅管理员）。
     *
     * @return 会议室列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<MeetingRoomDto>> listAll() {
        return ApiResponse.success(meetingRoomAdminService.listRooms());
    }

    /**
     * 查询指定时段空闲会议室。
     *
     * @param date      日期
     * @param startTime 开始时刻 HH:mm
     * @param endTime   结束时刻 HH:mm
     * @return 空闲会议室列表
     */
    @GetMapping("/available")
    public ApiResponse<List<MeetingRoomDto>> listAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime) {
        List<MeetingRoomDto> rooms = roomAvailabilityService.listAvailableRooms(date, startTime, endTime);
        return ApiResponse.success(rooms);
    }

    /**
     * 查询会议室某日占用片段。
     *
     * @param id   会议室 ID
     * @param date 日期
     * @return 占用片段
     */
    @GetMapping("/{id}/occupancy")
    public ApiResponse<List<OccupancySlotDto>> occupancy(
            @PathVariable("id") Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<OccupancySlotDto> slots = roomOccupancyService.listOccupancy(id, date);
        return ApiResponse.success(slots);
    }
}
