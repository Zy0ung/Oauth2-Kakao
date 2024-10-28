/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
package com.example.oauth2_kakao.repository;

import com.example.oauth2_kakao.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jiyoung
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
}