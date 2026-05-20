package com.meeting.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置，使用 BCrypt 校验与存储用户密码哈希。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 注册 BCrypt 密码编码器 Bean。
     *
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
