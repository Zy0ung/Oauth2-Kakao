/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
package com.example.oauth2_kakao.config;

import com.example.oauth2_kakao.service.CustomOAuth2UserService;
import com.example.oauth2_kakao.oauth2.CustomSuccessHandler;
import com.example.oauth2_kakao.jwt.JWTFilter;
import com.example.oauth2_kakao.jwt.JWTUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author jiyoung
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler,
            JWTUtil jwtUtil) {

        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                //csrf disable
                .csrf((auth) -> auth.disable())
                //From 로그인 방식 disable
                .formLogin((auth) -> auth.disable())
                //HTTP Basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())
                //oauth2
                .oauth2Login((oauth2) -> oauth2.userInfoEndpoint(
                                                       (userInfoEndpointConfig) -> userInfoEndpointConfig.userService(customOAuth2UserService))
                                               .successHandler(customSuccessHandler))
                // JWT 필터 추가 (OAuth2 인증 이후 수행 되도록 설정)
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class)
                //경로별 인가 작업
                .authorizeHttpRequests((auth) -> auth.requestMatchers("/").permitAll().anyRequest().authenticated())
                //세션 설정 : STATELESS
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}