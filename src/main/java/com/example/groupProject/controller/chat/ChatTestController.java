package com.example.groupProject.controller.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatTestController {

    @GetMapping("/stomp-test")
    public String stompTestPage() {
        return "stomp-test";
    }
}
