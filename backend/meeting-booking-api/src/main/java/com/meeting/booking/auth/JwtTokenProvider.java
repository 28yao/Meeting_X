package com.meeting.booking.auth;

import com.meeting.booking.user.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 令牌生成与解析组件，负责签发登录 Token 及校验请求中的 Bearer Token。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Component
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_DISPLAY_NAME = "displayName";

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 为指定用户生成 JWT。
     *
     * @param user 系统用户
     * @return JWT 字符串
     */
    public String createToken(SysUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_ROLE, user.getRole())
                .claim(CLAIM_DISPLAY_NAME, user.getDisplayName())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析并校验 JWT，失败时抛出 JwtException。
     *
     * @param token JWT 字符串
     * @return 登录主体信息
     */
    public LoginUserPrincipal parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        LoginUserPrincipal principal = new LoginUserPrincipal();
        principal.setUserId(((Number) claims.get(CLAIM_USER_ID)).longValue());
        principal.setUsername(claims.getSubject());
        principal.setRole((String) claims.get(CLAIM_ROLE));
        principal.setDisplayName((String) claims.get(CLAIM_DISPLAY_NAME));
        return principal;
    }

    /**
     * 判断 Token 是否可解析（不抛异常）。
     *
     * @param token JWT 字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }
}
