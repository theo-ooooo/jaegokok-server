package com.jaegokok.domain.auth;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(Long memberId, String token, long ttlSeconds);
    Optional<Long> findMemberIdByToken(String token);
    void deleteByToken(String token);
}
