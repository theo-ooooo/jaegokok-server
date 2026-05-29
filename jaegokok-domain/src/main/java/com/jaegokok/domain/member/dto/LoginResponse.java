package com.jaegokok.domain.member.dto;

public record LoginResponse(
        String accessToken,
        String nickname
) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.accessToken(), result.nickname());
    }
}
