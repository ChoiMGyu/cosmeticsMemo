package com.example.groupProject.service.authService;

import com.example.groupProject.config.util.JWTUtil;
import com.example.groupProject.domain.jwt.RefreshToken;
import com.example.groupProject.dto.jwt.TokenDto;
import com.example.groupProject.repository.jwt.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private static final Long ACCESS_TOKEN_TIME = 600000000L;
    private static final Long REFRESH_TOKEN_TIME = 86400000L;

    private static final String REFRESH_TOKEN_NULL_MESSAGE = "Refresh token is null";
    private static final String REFRESH_TOKEN_EXPIRED_MESSAGE = "Refresh token expired";
    private static final String REFRESH_TOKEN_CATEGORY_MESSAGE = "Invalid refresh token category";
    private static final String NOT_EXIST_REFRESH_TOKEN_MESSAGE = "Not Exist refresh token";

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    @Override
    public void addRefresh(String account, String refreshToken) {
        RefreshToken refresh = new RefreshToken(refreshToken, account);
        refreshTokenRepository.save(refresh);
    }

    @Override
    public TokenDto reissueToken(String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException(REFRESH_TOKEN_NULL_MESSAGE);
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException(REFRESH_TOKEN_EXPIRED_MESSAGE);
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            throw new IllegalArgumentException(REFRESH_TOKEN_CATEGORY_MESSAGE);
        }

        Boolean isExist = refreshTokenRepository.existsById(refreshToken);
        if (!isExist) {
            throw new IllegalArgumentException(NOT_EXIST_REFRESH_TOKEN_MESSAGE);
        }

        String account = jwtUtil.getAccount(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", account, role, ACCESS_TOKEN_TIME);
        String newRefreshToken = jwtUtil.createJwt("refresh", account, role, REFRESH_TOKEN_TIME);

        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        addRefresh(account, newRefreshToken);

        // 새로운 Access Token과 Refresh Token을 반환
        return new TokenDto(newAccessToken, newRefreshToken);
    }
}
