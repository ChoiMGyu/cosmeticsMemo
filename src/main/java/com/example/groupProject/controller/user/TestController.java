package com.example.groupProject.controller.user;

import com.example.groupProject.controller.memo.MemoApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(MemoApiController.class);

    @GetMapping("/not")
    public String method(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            logger.info("TestController - anonymous");
            return "anonymous";
        } else {
            logger.info("TestController - not anonymous");
            return "not anonymous";
        }
    }

    @GetMapping("/user")
    public String method(@CurrentSecurityContext SecurityContext context) {
        logger.info("TestController - Anonymous 유저를 반환한다");
        return context.getAuthentication().getName();
    }
}
