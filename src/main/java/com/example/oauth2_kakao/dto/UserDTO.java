package com.example.oauth2_kakao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jiyoung
 */
@Getter
@Setter
public class UserDTO {
    private String role;
    private String name;
    private String userName;
    private String email;
    private String profileImage;
}