package com.meeting.booking.user;

/**
 * 管理员用户管理默认值常量（新建与重置密码共用）。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class AdminUserDefaults {

    /**
     * 系统为新用户分配的默认明文密码（存储前经 BCrypt 加密）。
     */
    public static final String DEFAULT_PASSWORD = "123456";

    private AdminUserDefaults() {
    }
}
