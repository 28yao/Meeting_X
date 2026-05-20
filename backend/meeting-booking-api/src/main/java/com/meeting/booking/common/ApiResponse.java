package com.meeting.booking.common;

/**
 * 统一 REST API 响应封装，供 Controller 与全局异常处理返回一致 JSON 结构。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class ApiResponse<T> {

    private static final int CODE_SUCCESS = 0;

    private final int code;
    private final String message;
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构建成功响应。
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 成功响应体
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(CODE_SUCCESS, "success", data);
    }

    /**
     * 构建成功响应（无业务数据）。
     *
     * @return 成功响应体
     */
    public static ApiResponse<Void> success() {
        return success(null);
    }

    /**
     * 构建失败响应。
     *
     * @param code    业务错误码
     * @param message 错误说明
     * @param <T>     数据类型
     * @return 失败响应体
     */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<T>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
