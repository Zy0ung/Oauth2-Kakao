package com.example.oauth2_kakao.service;

import com.example.oauth2_kakao.dto.CustomOAuth2User;
import com.example.oauth2_kakao.dto.KakaoResponse;
import com.example.oauth2_kakao.dto.OAuth2Response;
import com.example.oauth2_kakao.dto.UserDTO;
import com.example.oauth2_kakao.entity.User;
import com.example.oauth2_kakao.repository.UserRepository;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;

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

        // 사용자명을 제공자_회원아이디 형식으로 만들어 저장하여 redis에서도 key로 쓸 예정
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        // 넘어온 회원정보가 이미 우리의 테이블에 존재하는지 확인
        User existData = userRepository.findByUserName(username);

        // 존재하지 않는다면 회원정보를 저장하고 CustomOAuth2User 반환
        if (existData == null) {
            User user = User.builder()
                    .provider(oAuth2Response.getProvider())
                    .providerId(oAuth2Response.getProviderId())
                    .userName(username)
                    .name(oAuth2Response.getName())
                    .profileImage(oAuth2Response.getProfileImage())
                    .email(oAuth2Response.getEmail())
                    .role("ROLE_USER")
                    .build();

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setProfileImage(oAuth2Response.getProfileImage());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        } else {
            // 회원정보가 존재한다면 조회된 데이터로 반환한다.
            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());
            existData.setProfileImage(oAuth2Response.getProfileImage());
            existData.setUpdateDate(new Date());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(username);
            userDTO.setName(existData.getName());
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setProfileImage(oAuth2Response.getProfileImage());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }
    }
}