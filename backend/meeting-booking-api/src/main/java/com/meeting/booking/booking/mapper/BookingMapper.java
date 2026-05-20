package com.meeting.booking.booking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meeting.booking.booking.dto.BookingMineRow;
import com.meeting.booking.booking.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约记录数据访问接口，含时段重叠检测 SQL。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Mapper
public interface BookingMapper extends BaseMapper<Booking> {

    /**
     * 统计某会议室在时段内与 CONFIRMED 预约重叠的数量。
     *
     * @param roomId    会议室 ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 重叠预约条数
     */
    @Select("SELECT COUNT(1) FROM booking "
            + "WHERE room_id = #{roomId} AND status = 'CONFIRMED' "
            + "AND start_time < #{endTime} AND end_time > #{startTime}")
    int countOverlap(@Param("roomId") Long roomId,
                     @Param("startTime") LocalDateTime startTime,
                     @Param("endTime") LocalDateTime endTime);

    /**
     * 查询某会议室某日有效预约（用于占用片段展示）。
     *
     * @param roomId 会议室 ID
     * @param dayStart 当日 00:00:00
     * @param dayEnd   次日 00:00:00
     * @return 预约列表
     */
    @Select("SELECT b.id, b.room_id AS roomId, b.organizer_id AS organizerId, b.title, "
            + "b.start_time AS startTime, b.end_time AS endTime, b.status, "
            + "b.created_at AS createdAt, b.updated_at AS updatedAt "
            + "FROM booking b "
            + "WHERE b.room_id = #{roomId} AND b.status = 'CONFIRMED' "
            + "AND b.start_time < #{dayEnd} AND b.end_time > #{dayStart} "
            + "ORDER BY b.start_time ASC")
    List<Booking> selectConfirmedByRoomAndDay(@Param("roomId") Long roomId,
                                              @Param("dayStart") LocalDateTime dayStart,
                                              @Param("dayEnd") LocalDateTime dayEnd);

    /**
     * 查询某组织者的全部预约（含会议室名称）。
     *
     * @param organizerId 组织者 ID
     * @return 预约行列表
     */
    @Select("SELECT b.id, b.room_id AS roomId, b.organizer_id AS organizerId, b.title, "
            + "b.start_time AS startTime, b.end_time AS endTime, b.status, "
            + "r.name AS roomName "
            + "FROM booking b "
            + "INNER JOIN meeting_room r ON b.room_id = r.id "
            + "WHERE b.organizer_id = #{organizerId}")
    List<BookingMineRow> selectByOrganizerIdWithRoom(@Param("organizerId") Long organizerId);

    /**
     * 将预约状态更新为已取消（仅 CONFIRMED 且属于指定组织者）。
     *
     * @param bookingId   预约 ID
     * @param organizerId 组织者 ID
     * @return 影响行数
     */
    @Update("UPDATE booking SET status = 'CANCELLED', updated_at = NOW() "
            + "WHERE id = #{bookingId} AND organizer_id = #{organizerId} AND status = 'CONFIRMED'")
    int updateStatusToCancelled(@Param("bookingId") Long bookingId,
                                @Param("organizerId") Long organizerId);

    /**
     * 统计某用户作为组织者的预约条数（含已取消）。
     *
     * @param organizerId 组织者用户 ID
     * @return 预约条数
     */
    @Select("SELECT COUNT(1) FROM booking WHERE organizer_id = #{organizerId}")
    int countByOrganizerId(@Param("organizerId") Long organizerId);
}
