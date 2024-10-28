package com.example.oauth2_kakao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

@SpringBootApplication
public class Oauth2KakaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2KakaoApplication.class, args);
	}
}