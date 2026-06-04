package com.jaegokok.infra.member;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.member.PasswordResetTokenEntity;
import com.jaegokok.domain.member.PasswordResetToken;
import com.jaegokok.domain.member.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;

    @Override
    public PasswordResetToken save(Long memberId) {
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.create(memberId);
        return toRecord(passwordResetTokenJpaRepository.save(entity));
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenJpaRepository.findByToken(token)
                .map(this::toRecord);
    }

    @Override
    public void markUsed(Long id) {
        PasswordResetTokenEntity entity = passwordResetTokenJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));
        entity.markUsed();
        passwordResetTokenJpaRepository.save(entity);
    }

    private PasswordResetToken toRecord(PasswordResetTokenEntity e) {
        return new PasswordResetToken(
                e.getId(),
                e.getMemberId(),
                e.getToken(),
                e.getExpiresAt(),
                e.isUsed()
        );
    }
}
