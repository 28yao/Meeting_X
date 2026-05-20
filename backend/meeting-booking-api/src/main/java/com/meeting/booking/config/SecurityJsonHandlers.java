package com.meeting.booking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.booking.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security 未认证/无权限时的 JSON 响应处理器，与 ApiResponse 格式保持一致。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Component
public class SecurityJsonHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final int CODE_UNAUTHORIZED = 40100;
    private static final int CODE_FORBIDDEN = 40300;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, CODE_UNAUTHORIZED, "未登录或登录已失效");
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        writeJson(response, HttpServletResponse.SC_FORBIDDEN, CODE_FORBIDDEN, "无权限访问");
    }

    private void writeJson(HttpServletResponse response, int status, int code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiResponse<Void> body = ApiResponse.fail(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
