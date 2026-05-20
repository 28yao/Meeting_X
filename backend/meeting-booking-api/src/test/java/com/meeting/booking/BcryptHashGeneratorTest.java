package com.meeting.booking;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 用于生成种子数据 BCrypt 哈希的临时测试类。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
class BcryptHashGeneratorTest {

    @Test
    void printAdminPasswordHash() {
        String hash = new BCryptPasswordEncoder().encode("admin123");
        System.out.println("BCrypt admin123: " + hash);
    }
}
