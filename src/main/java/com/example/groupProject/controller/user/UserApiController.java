package com.example.groupProject.controller.user;

import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.user.UserDto;
import com.example.groupProject.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {
    private static final String NOT_SAME_PASSWORD_MESSAGE = "패스워드가 일치하지 않습니다.";

    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    private final UserServiceImpl userService;

    @PostMapping("/new")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto) {
        logger.info("UserApiController - 회원 가입 요청");
        if (!userDto.getPassword().equals(userDto.getPassword_chk())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(NOT_SAME_PASSWORD_MESSAGE);
        }
        try {
            User user = User.createUser(userDto.getAccount(),
                    userDto.getPassword(),
                    userDto.getBirthdate(),
                    userDto.getSkinType(),
                    userDto.getNotification_opt(),
                    userDto.getSex(),
                    RoleType.ROLE_USER);
            Long userId = userService.join(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userId + ": " + userDto.getAccount() + "의 계정으로 회원 가입 되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
