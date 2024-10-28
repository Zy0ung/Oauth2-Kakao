/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
package com.example.oauth2_kakao.dto;

import java.util.Map;

/**
 * @author jiyoung
 */
public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getId() {
        // Extract the user ID from the "id" field in the response
        return attribute.containsKey("id") ? attribute.get("id").toString() : null;
    }

    @Override
    public String getEmail() {
        // Extract the email from the "kakao_account" -> "email" field
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        return kakaoAccount != null && kakaoAccount.containsKey("email") ? kakaoAccount.get("email").toString() : null;
    }

    @Override
    public String getName() {
        // Extract the name from the "properties" -> "nickname" field
        Map<String, Object> properties = (Map<String, Object>) attribute.get("properties");
        return properties != null && properties.containsKey("nickname") ? properties.get("nickname").toString() : null;
    }
}