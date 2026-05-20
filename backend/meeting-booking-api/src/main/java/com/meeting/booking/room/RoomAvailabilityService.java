package com.meeting.booking.room;

import com.meeting.booking.room.dto.MeetingRoomDto;
import com.meeting.booking.room.entity.MeetingRoom;
import com.meeting.booking.room.mapper.MeetingRoomMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 空闲会议室查询服务。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class RoomAvailabilityService {

    private final MeetingRoomMapper meetingRoomMapper;

    public RoomAvailabilityService(MeetingRoomMapper meetingRoomMapper) {
        this.meetingRoomMapper = meetingRoomMapper;
    }

    /**
     * 查询指定日期时段内可预约的空闲会议室。
     *
     * @param date      日期 yyyy-MM-dd
     * @param startTime 开始时刻 HH:mm
     * @param endTime   结束时刻 HH:mm
     * @return 空闲会议室 DTO 列表
     */
    public List<MeetingRoomDto> listAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);
        List<MeetingRoom> rooms = meetingRoomMapper.selectAvailableRooms(start, end);
        List<MeetingRoomDto> result = new ArrayList<MeetingRoomDto>(rooms.size());
        for (MeetingRoom room : rooms) {
            result.add(toDto(room));
        }
        return result;
    }

    private MeetingRoomDto toDto(MeetingRoom room) {
        MeetingRoomDto dto = new MeetingRoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setFloor(room.getFloor());
        dto.setRoomType(room.getRoomType());
        dto.setEquipment(room.getEquipment());
        dto.setStatus(room.getStatus());
        return dto;
    }
}
