package com.jaegokok.domain.member;

import java.util.Optional;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(Long memberId);
    Optional<PasswordResetToken> findByToken(String token);
    void markUsed(Long id);
}
