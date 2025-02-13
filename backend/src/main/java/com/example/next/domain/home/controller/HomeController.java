package com.example.next.domain.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "HomeController", description = "API 서버 홈")
@Controller
public class HomeController {

    @Operation(summary = "API 서버 시작페이지", description = "API 서버 시작페이지입니다. api 호출은 인증을 해주세요")
    @GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String home() {
        return "API 서버에 오신 걸 환영합니다.";
    }

}