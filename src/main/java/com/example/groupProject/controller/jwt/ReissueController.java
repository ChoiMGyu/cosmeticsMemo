package com.example.groupProject.controller.jwt;

import com.example.groupProject.dto.jwt.TokenDto;
import com.example.groupProject.service.authService.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private static final Logger logger = LoggerFactory.getLogger(ReissueController.class);
    private final JwtService jwtService;

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        logger.info("ReIssueController - 토큰 갱신 요청");
        try {
            String refreshToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refresh")) {
                        refreshToken = cookie.getValue();
                    }
                }
            }

            // TokenService에서 비즈니스 로직 수행 후 새로운 토큰 반환
            TokenDto newToken = jwtService.reissueToken(refreshToken);

            // 새 토큰을 Response에 설정
            response.setHeader("access", newToken.getAccessToken());
            response.addCookie(createCookie("refresh", newToken.getRefreshToken()));

            return ResponseEntity.status(HttpStatus.OK).body("정상적으로 Refresh Token이 갱신되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60 *60); //쿠키의 생명주기
        //cookie.setSecure(true); //https 통신을 사용할 경우
        //cookie.setPath("/"); //쿠키가 적용될 범위
        cookie.setHttpOnly(true);

        return cookie;
    }
}
