package com.meeting.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * 启用方法级安全注解（如 @PreAuthorize）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
