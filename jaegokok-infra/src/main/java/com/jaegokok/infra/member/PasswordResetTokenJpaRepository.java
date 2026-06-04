package com.jaegokok.infra.member;

import com.jaegokok.core.member.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByToken(String token);
}
