/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
package com.example.oauth2_kakao.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author jiyoung
 */

import java.util.Collections;

public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // UserDTO 객체를 통해 속성을 반환합니다.
        return Map.of("username", userDTO.getUsername(), "email", userDTO.getEmail(), "name", userDTO.getName(), "role",
                userDTO.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 권한을 반환합니다.
        return Collections.singletonList(() -> userDTO.getRole());
    }

    @Override
    public String getName() {
        // Spring Security에서 사용자 식별을 위해 getName() 메서드를 사용하므로 username을 반환합니다.
        return userDTO.getUsername();
    }

    public String getUsername() {
        return userDTO.getUsername();
    }
}