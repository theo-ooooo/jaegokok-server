package com.jaegokok.domain.member;

import java.time.LocalDateTime;

public record PasswordResetToken(
        Long id,
        Long memberId,
        String token,
        LocalDateTime expiresAt,
        boolean used
) {}
