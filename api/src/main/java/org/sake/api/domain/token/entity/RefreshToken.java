package org.sake.api.domain.token.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@NoArgsConstructor
@Data
@Getter
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 14)  // 14일
public class RefreshToken {

    @Id
    private String refreshToken;

    private Long userId;

    @Builder
    public RefreshToken(String refreshToken, Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}

