package com.example.groupProject.repository.jwt;

import com.example.groupProject.domain.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Ref;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    // Refresh 토큰이 존재하는지 확인하는 메서드
    boolean existsByRefreshToken(String refreshToken);

    // Refresh 토큰을 삭제하는 메서드
    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
