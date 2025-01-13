package com.example.groupProject.controller.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ChatTestController {

    @GetMapping("/stomp-test")
    public String stompTestPage() {
        log.info("웹 소켓만 테스트 하는 경우에 사용");
        return "stomp-test";
    }

    @GetMapping("/login")
    public String loginPage() {
        log.info("웹 소켓을 Access Token과 함께 테스트하는 경우에 사용");
        return "login";
    }
}
