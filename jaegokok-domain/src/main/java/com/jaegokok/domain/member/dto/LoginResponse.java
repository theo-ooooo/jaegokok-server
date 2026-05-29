package com.jaegokok.domain.member.dto;

public record LoginResponse(
        String accessToken,
        String nickname
) {}
