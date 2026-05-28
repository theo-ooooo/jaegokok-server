package com.jaegokok.infra.auth;

import com.jaegokok.domain.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Override
    public void save(Long memberId, String token, long ttlSeconds) {
        refreshTokenRedisRepository.save(RefreshTokenEntity.of(memberId, token, ttlSeconds));
    }

    @Override
    public Optional<Long> findMemberIdByToken(String token) {
        return refreshTokenRedisRepository.findByRefreshToken(token)
                .map(RefreshTokenEntity::getMemberId);
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRedisRepository.deleteByRefreshToken(token);
    }
}
