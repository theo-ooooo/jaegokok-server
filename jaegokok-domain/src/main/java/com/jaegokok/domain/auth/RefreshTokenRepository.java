package com.jaegokok.domain.auth;


import com.jaegokok.core.auth.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, Long> {
     void deleteByRefreshToken(String refreshToken);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);
}
