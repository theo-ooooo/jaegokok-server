package com.jaegokok.domain.member.dto;

public record LoginResult(
        String accessToken,
        String refreshToken,
        String nickname,
        long refreshTokenTtlSeconds
) {}
