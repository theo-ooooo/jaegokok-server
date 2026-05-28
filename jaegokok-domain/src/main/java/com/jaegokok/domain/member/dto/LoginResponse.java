package com.jaegokok.domain.member.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String nickname
) {}
