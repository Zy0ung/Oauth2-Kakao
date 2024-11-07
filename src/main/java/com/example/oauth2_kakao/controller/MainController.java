package com.example.oauth2_kakao.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author jiyoung
 */
@Controller
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainAPI() {
        return "main route";
    }

    @GetMapping("/success")
    @ResponseBody
    public String LoginSuccessAPI() {
        return "login success";
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .findFirst();

        if (!refreshCookie.isPresent()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String refreshToken = refreshCookie.get().getValue();
        if (refreshToken == null || refreshToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

//        String key = jwtUtil.getUsername(refreshToken);

//        if(redisService.getValues(key) == null) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        redisService.deleteValues(key);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.setStatus(HttpServletResponse.SC_OK);
        response.addCookie(cookie);

    }
}