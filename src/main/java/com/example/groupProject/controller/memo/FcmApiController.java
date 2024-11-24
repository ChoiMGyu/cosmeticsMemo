package com.example.groupProject.controller.memo;

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

    //알림 권한 요청이 왔을 때 허용을 할 경우 getToken()으로 가져온 디바이스 토큰을 등록한다
    //처음으로 로그인 하였을 때 물어 봐야 하고 html에서는 로그인 사용자에 대한 검사를 진행하지 않았지만 (프론트엔드)
    //백엔드에서 로그인을 하였는지 검사하고 (스프링 시큐리티에서 권한을 가진 사람만 호출 가능)
    //디바이스 토큰 등록을 호출한다

    @PostMapping("/tokenRegister")
    public ResponseEntity<String> registerDeviceToken(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody DeviceTokenRegisterDto deviceTokenRegisterDto) {
        logger.info("FcmApiController - User의 DeviceToken을 등록");

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        List<User> user = userService.findByAccount(customUserDetails.getUsername());
        fcmService.saveDeviceToken(deviceTokenRegisterDto.deviceToken(), user.get(DEVICE_TOKEN_MASTER));

        return ResponseEntity.ok("FCM을 위한 디바이스 토큰이 등록되었습니다.");
    }
}
