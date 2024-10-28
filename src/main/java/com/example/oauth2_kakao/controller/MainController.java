/*
 * Copyright ⓒ 2017 Brand X Corp. All Rights Reserved
 */
package com.example.oauth2_kakao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
}