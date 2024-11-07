package com.example.oauth2_kakao.jwt;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;

/**
 * @author jiyoung
 */
@Component
public class JWTUtil {

    private SecretKey secretKey;

    @Value("${app.auth.token-expiration-time}") // application.yml에서 만료 시간 가져오기
    private Long tokenExpirationTime; // 만료 시간을 밀리초 단위로 저장

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 사용자명 추출
    public String getUsername(String token) {
        return Jwts.parser()
                   .setSigningKey(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getBody()
                   .get("username", String.class);
    }

    // 권한 추출
    public String getRole(String token) {
        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .get("role", String.class);
    }

    // token 유효 확인
    public Boolean isExpired(String token) {
        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getExpiration()
                   .before(new Date());
    }

    // accessToken인지 refreshToken인지 확인
    public String getCategory(String name) {
        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(name)
                   .getPayload()
                   .get("category", String.class);
    }

    // JWT 발급
    public String createJwt(String category, String username, String role, Long expiredMs) {
        return Jwts.builder()
                   .claim("category", category)
                   .claim("username", username)
                   .claim("role", role)
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + expiredMs))
                   .signWith(secretKey)
                   .compact();
    }
}