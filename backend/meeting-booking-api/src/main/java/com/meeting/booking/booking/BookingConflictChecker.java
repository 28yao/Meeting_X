package com.meeting.booking.booking;

import com.meeting.booking.booking.mapper.BookingMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 预约时段冲突检测，基于时间重叠 SQL 查询。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Component
public class BookingConflictChecker {

    private final BookingMapper bookingMapper;

    public BookingConflictChecker(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    /**
     * 判断某会议室在时段内是否存在重叠的有效预约。
     *
     * @param roomId    会议室 ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 存在重叠返回 true
     */
    public boolean hasOverlap(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingMapper.countOverlap(roomId, startTime, endTime) > 0;
    }

    /**
     * 统计重叠预约数量（用于并发插入后二次校验）。
     *
     * @param roomId    会议室 ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 重叠条数
     */
    public int countOverlap(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingMapper.countOverlap(roomId, startTime, endTime);
    }
}
