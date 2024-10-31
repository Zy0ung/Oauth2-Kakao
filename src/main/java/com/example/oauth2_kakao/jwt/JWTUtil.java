/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
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
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public String getUsername(String token) {
        return Jwts.parser()
                   .setSigningKey(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getBody()
                   .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                   .setSigningKey(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getBody()
                   .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        Date expirationDate =
                Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token).getBody().getExpiration();
        return expirationDate.before(new Date());
    }

    public String createJwt(String username, String role) {
        // 현재 시간을 UTC 기준으로 가져온다.
        Date now = new Date(System.currentTimeMillis());

        // 만료 시간을 한국 표준시(KST)로 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MILLISECOND, Math.toIntExact(tokenExpirationTime)); // 설정된 만료 시간 추가

        Date expirationDate = calendar.getTime(); // 최종 만료 시간

        return Jwts.builder().claim("username", username).claim("role", role).setIssuedAt(now) // 현재 시간
                   .setExpiration(expirationDate) // 만료 시간
                   .signWith(secretKey).compact();
    }
}