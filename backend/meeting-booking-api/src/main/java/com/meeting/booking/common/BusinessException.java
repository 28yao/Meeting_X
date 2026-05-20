package com.meeting.booking.common;

/**
 * 业务异常，携带业务错误码与 HTTP 状态，由全局异常处理器转换为统一响应。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class BusinessException extends RuntimeException {

    private final int code;
    private final int httpStatus;

    /**
     * 构造业务异常。
     *
     * @param code       业务错误码
     * @param message    错误说明
     * @param httpStatus HTTP 状态码
     */
    public BusinessException(int code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
