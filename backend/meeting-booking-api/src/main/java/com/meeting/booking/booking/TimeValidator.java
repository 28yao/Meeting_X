package com.meeting.booking.booking;

import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * 预约时间规则校验器（TR-01～TR-05）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Component
public class TimeValidator {

    private static final int SLOT_MINUTES = 15;
    private static final int MAX_ADVANCE_DAYS = 30;

    /**
     * 校验预约开始与结束时间是否符合全部时间规则。
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void validate(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw business(ErrorCodes.INVALID_TIME_RANGE, "开始与结束时间不能为空");
        }
        if (!endTime.isAfter(startTime)) {
            throw business(ErrorCodes.INVALID_TIME_RANGE, "结束时间必须晚于开始时间");
        }
        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            throw business(ErrorCodes.CROSS_DAY_NOT_ALLOWED, "不允许跨天预约，请选择同一天内的时段");
        }
        if (!isAligned(startTime) || !isAligned(endTime)) {
            throw business(ErrorCodes.TIME_SLOT_NOT_ALIGNED, "时间须按 15 分钟对齐（如 10:00、10:15）");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw business(ErrorCodes.PAST_TIME_NOT_ALLOWED, "不能预约过去的时间");
        }
        LocalDate maxDate = LocalDate.now().plusDays(MAX_ADVANCE_DAYS);
        if (startTime.toLocalDate().isAfter(maxDate)) {
            throw business(ErrorCodes.EXCEED_ADVANCE_LIMIT, "最多只能提前 30 天预约");
        }
    }

    private boolean isAligned(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        return dateTime.getSecond() == 0 && dateTime.getNano() == 0
                && minute % SLOT_MINUTES == 0;
    }

    private BusinessException business(int code, String message) {
        return new BusinessException(code, message, HttpStatus.BAD_REQUEST.value());
    }
}
