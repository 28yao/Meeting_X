package com.meeting.booking.room;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meeting.booking.booking.mapper.BookingMapper;
import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import com.meeting.booking.room.dto.CreateAdminRoomRequest;
import com.meeting.booking.room.dto.MeetingRoomDto;
import com.meeting.booking.room.dto.UpdateAdminRoomRequest;
import com.meeting.booking.room.entity.MeetingRoom;
import com.meeting.booking.room.mapper.MeetingRoomMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 管理员会议室管理业务服务：列表、创建、编辑、状态变更与逻辑删除。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class MeetingRoomAdminService {

    private final MeetingRoomMapper meetingRoomMapper;
    private final BookingMapper bookingMapper;

    public MeetingRoomAdminService(MeetingRoomMapper meetingRoomMapper, BookingMapper bookingMapper) {
        this.meetingRoomMapper = meetingRoomMapper;
        this.bookingMapper = bookingMapper;
    }

    /**
     * 查询全部未删除会议室（按名称升序）。
     *
     * @return 会议室 DTO 列表
     */
    public List<MeetingRoomDto> listRooms() {
        LambdaQueryWrapper<MeetingRoom> wrapper = new LambdaQueryWrapper<MeetingRoom>()
                .orderByAsc(MeetingRoom::getName);
        List<MeetingRoom> rooms = meetingRoomMapper.selectList(wrapper);
        if (rooms == null || rooms.isEmpty()) {
            return Collections.emptyList();
        }
        List<MeetingRoomDto> result = new ArrayList<MeetingRoomDto>(rooms.size());
        for (MeetingRoom room : rooms) {
            result.add(toDto(room));
        }
        return result;
    }

    /**
     * 创建会议室。
     *
     * @param request 创建请求
     * @return 新会议室 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public MeetingRoomDto createRoom(CreateAdminRoomRequest request) {
        MeetingRoom room = new MeetingRoom();
        room.setName(trim(request.getName()));
        room.setCapacity(request.getCapacity());
        room.setFloor(trimToNull(request.getFloor()));
        room.setRoomType(trimToNull(request.getRoomType()));
        room.setEquipment(trimToNull(request.getEquipment()));
        room.setStatus(RoomStatus.NORMAL);
        meetingRoomMapper.insert(room);
        return toDto(room);
    }

    /**
     * 更新会议室信息（不含状态）。
     *
     * @param roomId  会议室 ID
     * @param request 更新请求
     * @return 更新后 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public MeetingRoomDto updateRoom(Long roomId, UpdateAdminRoomRequest request) {
        MeetingRoom room = requireRoom(roomId);
        if (request.getName() != null) {
            room.setName(trim(request.getName()));
        }
        if (request.getCapacity() != null) {
            room.setCapacity(request.getCapacity());
        }
        if (request.getFloor() != null) {
            room.setFloor(trimToNull(request.getFloor()));
        }
        if (request.getRoomType() != null) {
            room.setRoomType(trimToNull(request.getRoomType()));
        }
        if (request.getEquipment() != null) {
            room.setEquipment(trimToNull(request.getEquipment()));
        }
        meetingRoomMapper.updateById(room);
        return toDto(room);
    }

    /**
     * 更新会议室状态（NORMAL / MAINTENANCE）。
     *
     * @param roomId 会议室 ID
     * @param status 目标状态
     * @return 更新后 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public MeetingRoomDto updateStatus(Long roomId, String status) {
        validateStatus(status);
        MeetingRoom room = requireRoom(roomId);
        room.setStatus(status.trim());
        meetingRoomMapper.updateById(room);
        return toDto(room);
    }

    /**
     * 逻辑删除会议室；存在未来有效预约时禁止删除。
     *
     * @param roomId 会议室 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(Long roomId) {
        requireRoom(roomId);
        int futureCount = bookingMapper.countFutureBookingsByRoomId(roomId);
        if (futureCount > 0) {
            throw new BusinessException(ErrorCodes.ROOM_HAS_FUTURE_BOOKINGS,
                    "该会议室有 " + futureCount + " 条未来预约，无法删除",
                    HttpStatus.CONFLICT.value());
        }
        int rows = meetingRoomMapper.deleteById(roomId);
        if (rows == 0) {
            throw new BusinessException(ErrorCodes.ROOM_NOT_FOUND, "会议室不存在",
                    HttpStatus.NOT_FOUND.value());
        }
    }

    private MeetingRoom requireRoom(Long roomId) {
        MeetingRoom room = meetingRoomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException(ErrorCodes.ROOM_NOT_FOUND, "会议室不存在",
                    HttpStatus.NOT_FOUND.value());
        }
        return room;
    }

    private void validateStatus(String status) {
        if (status == null || !RoomStatus.isValid(status.trim())) {
            throw new BusinessException(40007, "状态必须为 NORMAL 或 MAINTENANCE",
                    HttpStatus.BAD_REQUEST.value());
        }
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

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        String trimmed = trim(value);
        if (trimmed == null || trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }
}
