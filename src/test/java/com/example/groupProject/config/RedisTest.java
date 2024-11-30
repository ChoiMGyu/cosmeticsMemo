package com.example.groupProject.config;

import com.example.groupProject.repository.jwt.RefreshTokenRepository;
import com.example.groupProject.service.authService.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("Redis 연결 테스트")
    public void 레디스_환경_테스트() throws Exception
    {
        //given
        ValueOperations<String, String> valueOperaions = redisTemplate.opsForValue();
        String key = "name";
        valueOperaions.set(key, "giraffe");

        //when
        String value = valueOperaions.get(key);

        //then
        Assertions.assertEquals(value, "giraffe");
    }

    @Test
    @DisplayName("레디스 키 형식 확인")
    public void 레디스_저장형식() throws Exception
    {
        //given
        String account = "user";
        String refreshToken = "abcde";

        //when
        jwtService.addRefresh(account, refreshToken);
        boolean result = refreshTokenRepository.existsById(refreshToken);

        //then
        Assertions.assertTrue(result);
    }


}
