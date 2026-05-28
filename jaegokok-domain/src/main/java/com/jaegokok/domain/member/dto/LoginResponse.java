package com.jaegokok.domain.member.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String nickname
) {
    public static LoginResponse from(String accessToken, String refreshToken, String nickname) {
        return new LoginResponse(accessToken, refreshToken, nickname);
    }
}
