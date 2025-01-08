package com.example.groupProject.redis.jwt;

import com.example.groupProject.domain.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    boolean existsByRefreshToken(String refreshToken);

    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
