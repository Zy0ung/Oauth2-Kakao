package com.example.oauth2_kakao.entity;

import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author jiyoung
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "user_tbl")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 자동으로 생성되는 인덱스

    @Column(name = "provider", length = 50)
    private String provider; // 제공자

    @Column(name = "provider_id", length = 100)
    private String providerId; // 제공자 ID

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "name", length = 100)
    @NotNull
    private String name; // 이름

    @Column(name = "email", unique = true, length = 100)
    @NotNull
    private String email; // 이메일

    @Column(name = "profile_image", length = 255)
    private String profileImage; // 프로필 이미지

    @Column(name = "role", nullable = false, length = 50)
    private String role; // 역할

    @Column(name = "reg_date")
    private Date regDate; // 생성 날짜

    @Column(name = "update_date")
    private Date updateDate; // 수정 날짜

    @PrePersist
    protected void onCreate() {
        this.regDate = new Date();
        this.updateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = new Date();
    }

    @Builder
    public User(String provider, String providerId, String userName, String name, String email, String role, String profileImage) {
        this.provider = provider;
        this.providerId = providerId;
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImage = profileImage;
    }
}