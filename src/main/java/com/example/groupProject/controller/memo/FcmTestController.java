package com.example.groupProject.controller.memo;

import com.example.groupProject.config.FcmConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FcmTestController {

    private final FcmConfig init;

    @GetMapping("/alarm")
    public String v1() {
        init.initialize();
        return "firebase-snippert";
    }
}
