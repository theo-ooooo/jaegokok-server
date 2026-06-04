package com.jaegokok.core.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false, updatable = false,
            insertable = false)
    private LocalDateTime createdAt;

    public static PasswordResetTokenEntity create(Long memberId) {
        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.memberId = memberId;
        entity.token = UUID.randomUUID().toString();
        entity.expiresAt = LocalDateTime.now().plusMinutes(30);
        entity.used = false;
        return entity;
    }

    public void markUsed() {
        this.used = true;
    }
}
