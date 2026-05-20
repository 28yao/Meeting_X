package com.meeting.booking.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 集成测试用唯一预约时段生成器，避免共享数据库时段冲突。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class BookingTestSlots {

    private BookingTestSlots() {
    }

    /**
     * 生成唯一未来预约开始时间（15 分钟对齐，满足提前 30 天内规则）。
     *
     * @return 开始时间
     */
    public static LocalDateTime nextStart() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int dayOffset = random.nextInt(4, 28);
        int hour = random.nextInt(8, 18);
        int minute = random.nextInt(0, 4) * 15;
        return LocalDate.now().plusDays(dayOffset).atTime(hour, minute, 0);
    }
}
