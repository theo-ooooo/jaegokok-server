package com.jaegokok.infra.auth;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}
