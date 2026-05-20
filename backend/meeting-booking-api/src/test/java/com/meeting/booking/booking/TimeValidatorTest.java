package com.meeting.booking.booking;

import com.meeting.booking.common.BusinessException;
import com.meeting.booking.common.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TimeValidator 单元测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
class TimeValidatorTest {

    private TimeValidator timeValidator;

    @BeforeEach
    void setUp() {
        timeValidator = new TimeValidator();
    }

    @Test
    void validRangePasses() {
        LocalDateTime start = nextAlignedSlot(30);
        LocalDateTime end = start.plusMinutes(30);
        timeValidator.validate(start, end);
    }

    @Test
    void invalidRangeThrows40001() {
        LocalDateTime t = nextAlignedSlot(30);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> timeValidator.validate(t, t));
        assertEquals(ErrorCodes.INVALID_TIME_RANGE, ex.getCode());
    }

    @Test
    void crossDayThrows40002() {
        LocalDateTime start = nextAlignedSlot(30);
        LocalDateTime end = start.plusDays(1);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> timeValidator.validate(start, end));
        assertEquals(ErrorCodes.CROSS_DAY_NOT_ALLOWED, ex.getCode());
    }

    @Test
    void notAlignedThrows40003() {
        LocalDateTime start = nextAlignedSlot(30).withMinute(10);
        LocalDateTime end = start.plusMinutes(30);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> timeValidator.validate(start, end));
        assertEquals(ErrorCodes.TIME_SLOT_NOT_ALIGNED, ex.getCode());
    }

    @Test
    void pastTimeThrows40004() {
        LocalDateTime start = LocalDateTime.now().minusHours(2).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(30);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> timeValidator.validate(start, end));
        assertEquals(ErrorCodes.PAST_TIME_NOT_ALLOWED, ex.getCode());
    }

    @Test
    void exceedAdvanceThrows40005() {
        LocalDateTime start = LocalDateTime.now().plusDays(31).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(30);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> timeValidator.validate(start, end));
        assertEquals(ErrorCodes.EXCEED_ADVANCE_LIMIT, ex.getCode());
    }

    private LocalDateTime nextAlignedSlot(int minutesFromNow) {
        LocalDateTime base = LocalDateTime.now().plusMinutes(minutesFromNow);
        int minute = base.getMinute();
        int aligned = ((minute / 15) + 1) * 15;
        if (aligned >= 60) {
            return base.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        }
        return base.withMinute(aligned).withSecond(0).withNano(0);
    }
}
