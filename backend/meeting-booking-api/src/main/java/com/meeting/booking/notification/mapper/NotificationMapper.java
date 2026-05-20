package com.meeting.booking.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meeting.booking.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 站内通知数据访问接口。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 查询用户通知列表（按创建时间降序）。
     *
     * @param userId 用户 ID
     * @return 通知列表
     */
    @Select("SELECT id, user_id AS userId, type, title, content, related_booking_id AS relatedBookingId, "
            + "read_flag AS readFlag, created_at AS createdAt "
            + "FROM notification WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Notification> selectByUserId(@Param("userId") Long userId);

    /**
     * 统计用户未读通知数量。
     *
     * @param userId 用户 ID
     * @return 未读条数
     */
    @Select("SELECT COUNT(1) FROM notification WHERE user_id = #{userId} AND read_flag = 0")
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 将单条通知标为已读（仅属于指定用户）。
     *
     * @param id     通知 ID
     * @param userId 用户 ID
     * @return 影响行数
     */
    @Update("UPDATE notification SET read_flag = 1 WHERE id = #{id} AND user_id = #{userId} AND read_flag = 0")
    int markReadByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 将用户全部未读通知标为已读。
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    @Update("UPDATE notification SET read_flag = 1 WHERE user_id = #{userId} AND read_flag = 0")
    int markAllReadByUserId(@Param("userId") Long userId);
}
