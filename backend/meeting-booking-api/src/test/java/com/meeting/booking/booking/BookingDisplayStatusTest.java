package com.meeting.booking.booking;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 预约展示状态推导单元测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
class BookingDisplayStatusTest {

    @Test
    void deriveUpcoming() {
        LocalDateTime now = LocalDateTime.of(2026, 5, 20, 10, 0);
        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = start.plusMinutes(30);
        assertEquals(BookingDisplayStatus.UPCOMING,
                BookingDisplayStatus.derive("CONFIRMED", start, end, now));
    }

    @Test
    void deriveCancelled() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(BookingDisplayStatus.CANCELLED,
                BookingDisplayStatus.derive("CANCELLED", now, now.plusHours(1), now));
    }

    @Test
    void cancellableOnlyUpcomingConfirmed() {
        assertTrue(BookingDisplayStatus.cancellable("CONFIRMED", BookingDisplayStatus.UPCOMING));
        assertFalse(BookingDisplayStatus.cancellable("CONFIRMED", BookingDisplayStatus.IN_PROGRESS));
        assertFalse(BookingDisplayStatus.cancellable("CANCELLED", BookingDisplayStatus.CANCELLED));
    }

    @Test
    void groupOrderUpcomingBeforeEnded() {
        assertTrue(BookingDisplayStatus.groupOrder(BookingDisplayStatus.UPCOMING)
                < BookingDisplayStatus.groupOrder(BookingDisplayStatus.ENDED));
    }
}
