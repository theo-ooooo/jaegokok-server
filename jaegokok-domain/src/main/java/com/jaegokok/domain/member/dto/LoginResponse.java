package com.jaegokok.domain.member.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String nickname,
        Long refreshTokenTtlSeconds
) {
    public static LoginResponse from(String accessToken, String refreshToken, String nickname, Long refreshTokenTtlSeconds) {
        return new LoginResponse(accessToken, refreshToken, nickname, refreshTokenTtlSeconds);
    }
}
