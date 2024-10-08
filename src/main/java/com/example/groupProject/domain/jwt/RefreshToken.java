package com.example.groupProject.domain.jwt;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440) //4시간
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RefreshToken {

    @Id //jakarta.persistence.Id가 아니고 NoSQL이므로 GeneratedValue
    private String refreshToken;
    private String account;
}
