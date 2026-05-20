package com.meeting.booking.room.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meeting.booking.room.entity.MeetingRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议室数据访问接口。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Mapper
public interface MeetingRoomMapper extends BaseMapper<MeetingRoom> {

    /**
     * 查询指定时段内无冲突预约的空闲会议室（维护中、已删除除外）。
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 空闲会议室列表
     */
    @Select("SELECT id, name, capacity, floor, room_type AS roomType, equipment, status, deleted, "
            + "created_at AS createdAt, updated_at AS updatedAt "
            + "FROM meeting_room r "
            + "WHERE r.deleted = 0 AND r.status = 'NORMAL' "
            + "AND NOT EXISTS ( "
            + "  SELECT 1 FROM booking b "
            + "  WHERE b.room_id = r.id AND b.status = 'CONFIRMED' "
            + "  AND b.start_time < #{endTime} AND b.end_time > #{startTime} "
            + ") "
            + "ORDER BY r.name ASC")
    List<MeetingRoom> selectAvailableRooms(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
}
