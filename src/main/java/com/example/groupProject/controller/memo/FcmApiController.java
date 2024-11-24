package com.example.groupProject.controller.memo;

import com.example.groupProject.controller.message.ErrorMessage;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.jwt.CustomUserDetails;
import com.example.groupProject.dto.memo.DeviceTokenRegisterDto;
import com.example.groupProject.service.UserServiceImpl;
import com.example.groupProject.service.memo.FcmService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmApiController {
    private static final int DEVICE_TOKEN_MASTER = 0;

    private static final Logger logger = LoggerFactory.getLogger(FcmApiController.class);

    private final FcmService fcmService;
    private final UserServiceImpl userService;

    @PostMapping("/tokenRegister")
    public ResponseEntity<String> registerDeviceToken(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody DeviceTokenRegisterDto deviceTokenRegisterDto) {
        logger.info("FcmApiController - User의 DeviceToken을 등록");

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        List<User> user = userService.findByAccount(customUserDetails.getUsername());
        fcmService.saveDeviceToken(deviceTokenRegisterDto.deviceToken(), user.get(DEVICE_TOKEN_MASTER));

        return ResponseEntity.ok("FCM을 위한 디바이스 토큰이 등록되었습니다.");
    }
}
