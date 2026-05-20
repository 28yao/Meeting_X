package com.meeting.booking.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，将业务异常与校验异常转换为统一 ApiResponse JSON。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final int CODE_INTERNAL_ERROR = 50000;
    private static final int CODE_VALIDATION_ERROR = 40000;

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常。
     *
     * @param ex 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        ApiResponse<Void> body = ApiResponse.fail(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    /**
     * 处理参数校验异常。
     *
     * @param ex 校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationException(Exception ex) {
        String message = "请求参数不合法";
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validEx = (MethodArgumentNotValidException) ex;
            if (validEx.getBindingResult().getFieldError() != null) {
                message = validEx.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        if (ex instanceof BindException) {
            BindException bindEx = (BindException) ex;
            if (bindEx.getBindingResult().getFieldError() != null) {
                message = bindEx.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        ApiResponse<Void> body = ApiResponse.fail(CODE_VALIDATION_ERROR, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 处理未捕获的系统异常。
     *
     * @param ex 系统异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        LOG.error("Unhandled exception", ex);
        ApiResponse<Void> body = ApiResponse.fail(CODE_INTERNAL_ERROR, "系统繁忙，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
