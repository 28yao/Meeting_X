package com.meeting.booking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 会议室预约系统后端应用启动入口。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@SpringBootApplication
@MapperScan("com.meeting.booking.**.mapper")
public class MeetingBookingApplication {

    /**
     * 应用主入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MeetingBookingApplication.class, args);
    }
}
