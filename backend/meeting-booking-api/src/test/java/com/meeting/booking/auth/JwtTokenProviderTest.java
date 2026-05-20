package com.meeting.booking.auth;

import com.meeting.booking.user.entity.SysUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JWT 生成与解析单元测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret",
                "meeting-booking-jwt-secret-key-for-dev-only-32b");
        ReflectionTestUtils.setField(jwtTokenProvider, "expirationMs", 3600000L);
        jwtTokenProvider.init();
    }

    @Test
    void createAndParseToken() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setDisplayName("系统管理员");
        user.setRole("ADMIN");

        String token = jwtTokenProvider.createToken(user);
        assertTrue(jwtTokenProvider.validateToken(token));

        LoginUserPrincipal principal = jwtTokenProvider.parseToken(token);
        assertEquals(Long.valueOf(1L), principal.getUserId());
        assertEquals("admin", principal.getUsername());
        assertEquals("ADMIN", principal.getRole());
        assertEquals("系统管理员", principal.getDisplayName());
    }
}
