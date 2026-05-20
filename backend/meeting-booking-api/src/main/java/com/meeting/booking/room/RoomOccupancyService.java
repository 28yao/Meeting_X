package com.meeting.booking.room;

import com.meeting.booking.booking.entity.Booking;
import com.meeting.booking.booking.mapper.BookingMapper;
import com.meeting.booking.room.dto.OccupancySlotDto;
import com.meeting.booking.user.entity.SysUser;
import com.meeting.booking.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 会议室某日占用片段查询服务。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class RoomOccupancyService {

    private final BookingMapper bookingMapper;
    private final SysUserMapper sysUserMapper;

    public RoomOccupancyService(BookingMapper bookingMapper, SysUserMapper sysUserMapper) {
        this.bookingMapper = bookingMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 查询会议室在某日的占用片段（含主题、组织者显示名）。
     *
     * @param roomId 会议室 ID
     * @param date   日期
     * @return 占用片段列表
     */
    public List<OccupancySlotDto> listOccupancy(Long roomId, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Booking> bookings = bookingMapper.selectConfirmedByRoomAndDay(roomId, dayStart, dayEnd);
        List<OccupancySlotDto> result = new ArrayList<OccupancySlotDto>(bookings.size());
        for (Booking booking : bookings) {
            OccupancySlotDto slot = new OccupancySlotDto();
            slot.setTitle(booking.getTitle());
            slot.setStartTime(booking.getStartTime());
            slot.setEndTime(booking.getEndTime());
            SysUser organizer = sysUserMapper.selectById(booking.getOrganizerId());
            slot.setOrganizerName(organizer != null ? organizer.getDisplayName() : "未知用户");
            result.add(slot);
        }
        return result;
    }
}
