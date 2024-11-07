package com.example.oauth2_kakao.dto;

/**
 * @author jiyoung
 */
public interface OAuth2Response {
    /**
     * 제공자
     */
    String getProvider();

    /**
     * 제공자가 발급해주는 번호
     */
    String getProviderId();

    /**
     * 이메일
     */
    String getEmail();

    /**
     * 사용자 이름
     */
    String getName();

    /**
     * 프로필 이미지
     */
    String getProfileImage();
}
