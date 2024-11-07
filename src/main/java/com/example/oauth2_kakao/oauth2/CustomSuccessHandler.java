package com.example.oauth2_kakao.oauth2;

import com.example.oauth2_kakao.dto.CustomOAuth2User;
import com.example.oauth2_kakao.jwt.JWTUtil;

import com.example.oauth2_kakao.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
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
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        //OAuth2User를 CustomOAuth2User로 캐스팅하여 사용자 정보 가져오기
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        // 카카오 ID를 포함하는 사용자 이름 가져오기
        String username = customUserDetails.getUserName();

        // 사용자의 권한 목록 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // accessToken과 refreshToken 생성
        String accessToken = jwtUtil.createJwt("access", username, role, 60000L);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);

        //redis에 insert (Key = username / value = refreshToken)
        redisService.setValues(username, refreshToken, Duration.ofMillis(86400000L));

        // 응답
        response.setHeader("access", "Bearer " + accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        System.out.println(refreshToken);
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("/success");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 쿠키가 살아 있을 시간 (24시간)
        // cookie.setSecure(true); https에서만 동작할 것인지 (로컬은 http 환경)
        // cookie.setPath("/"); 쿠키가 전역에서 동작
        cookie.setHttpOnly(true); // http에서만 쿠키가 동작할 수 있도록 (js와 같은 곳에서 가져갈 수 없도록 설정)

        return cookie;
    }
}