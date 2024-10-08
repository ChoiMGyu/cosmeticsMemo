package com.example.groupProject.service.AuthService;

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
            throw new IllegalArgumentException("Refresh token is null");
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            throw new IllegalArgumentException("Invalid refresh token category");
        }

        Boolean isExist = refreshTokenRepository.existsById(refreshToken);
        if (!isExist) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String account = jwtUtil.getAccount(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", account, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", account, role, 86400000L);

        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        addRefresh(account, newRefreshToken);

        // 새로운 Access Token과 Refresh Token을 반환
        return new TokenDto(newAccessToken, newRefreshToken);
    }
}
