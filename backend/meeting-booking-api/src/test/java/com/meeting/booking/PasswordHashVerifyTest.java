package com.meeting.booking;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 校验种子数据中 admin 密码哈希是否与 admin123 匹配。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
class PasswordHashVerifyTest {

    @Test
    void seedHashMatchesAdmin123() {
        String hash = "$2a$10$xj.416xSOWqeg26cUR/xouxQv9aw60jfIRK9vxfsSPIU9bQVYRYka";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("admin123", hash), "种子哈希与 admin123 不匹配");
    }
}
