package com.meeting.booking.user;

/**
 * 系统用户角色常量。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public final class UserRole {

    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String ADMIN = "ADMIN";

    private UserRole() {
    }

    /**
     * 判断是否为合法角色值。
     *
     * @param role 角色字符串
     * @return 合法为 true
     */
    public static boolean isValid(String role) {
        return EMPLOYEE.equals(role) || ADMIN.equals(role);
    }
}
