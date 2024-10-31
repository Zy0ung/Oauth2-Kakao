/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
package com.example.oauth2_kakao.oauth2;

import com.example.oauth2_kakao.dto.CustomOAuth2User;
import com.example.oauth2_kakao.jwt.JWTUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author jiyoung
 */
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        //OAuth2User를 CustomOAuth2User로 캐스팅하여 사용자 정보 가져오기
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        // 카카오 ID를 포함하는 사용자 이름 가져오기
        String username = customUserDetails.getUsername();

        // 사용자의 권한 목록 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        String role = iterator.hasNext() ? iterator.next().getAuthority() : "ROLE_USER";

        // JWT 토큰 생성 (예: 1시간 유효기간)
        String token = jwtUtil.createJwt(username, role);

        // Authorization 쿠키 추가
        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("/success");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}