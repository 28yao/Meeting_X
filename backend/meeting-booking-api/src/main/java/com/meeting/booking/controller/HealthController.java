package com.meeting.booking.controller;

import com.meeting.booking.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查接口，用于验证服务已启动且可正常响应 HTTP 请求。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@RestController
public class HealthController {

    /**
     * 健康检查：返回服务状态与当前时间戳。
     *
     * @return 统一成功响应，data 含 status 与 timestamp
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<String, Object>(2);
        data.put("status", "UP");
        data.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(data);
    }
}
