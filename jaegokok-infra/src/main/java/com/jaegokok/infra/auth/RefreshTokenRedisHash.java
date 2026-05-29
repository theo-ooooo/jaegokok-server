package com.jaegokok.infra.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenRedisHash {

    @Id
    private Long memberId;

    @Indexed
    private String refreshToken;

    @TimeToLive
    private Long ttl;

    @Builder
    private RefreshTokenRedisHash(Long memberId, String refreshToken, Long ttl) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
        this.ttl = ttl;
    }

    public static RefreshTokenRedisHash of(Long memberId, String refreshToken, long ttl) {
        return RefreshTokenRedisHash.builder()
                .memberId(memberId)
                .refreshToken(refreshToken)
                .ttl(ttl)
                .build();
    }
}
