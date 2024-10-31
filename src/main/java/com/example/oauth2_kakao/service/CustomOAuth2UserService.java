/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
package com.example.oauth2_kakao.service;

import com.example.oauth2_kakao.dto.CustomOAuth2User;
import com.example.oauth2_kakao.dto.UserDTO;
import com.example.oauth2_kakao.dto.KakaoResponse;
import com.example.oauth2_kakao.dto.OAuth2Response;
import com.example.oauth2_kakao.entity.UserEntity;
import com.example.oauth2_kakao.repository.UserRepository;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * @author jiyoung
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        String principalName = null;

        // Kakao로부터 사용자 정보를 처리
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            Object idAttribute = oAuth2User.getAttribute("id");
            principalName = String.valueOf(idAttribute);
        }

        if (principalName == null || principalName.isEmpty()) {
            throw new IllegalArgumentException("principalName이 비어있습니다.");
        }

        UserEntity existData = userRepository.findByUsername(oAuth2Response.getName());

        // 새로운 사용자일 경우
        if (existData == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(oAuth2Response.getName());
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        } else {
            // 기존 사용자일 경우 정보 업데이트
            existData.setEmail(oAuth2Response.getEmail());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setRole(existData.getRole());

            return new CustomOAuth2User(userDTO);
        }
    }
}