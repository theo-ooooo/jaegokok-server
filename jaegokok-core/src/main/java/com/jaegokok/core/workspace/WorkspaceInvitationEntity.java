package com.jaegokok.core.workspace;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workspace_invitations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceInvitationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkspaceMemberRole role = WorkspaceMemberRole.EMPLOYEE;

    public static WorkspaceInvitationEntity create(Long workspaceId, String email, WorkspaceMemberRole role) {
        WorkspaceInvitationEntity entity = new WorkspaceInvitationEntity();
        entity.workspaceId = workspaceId;
        entity.email = email;
        entity.token = UUID.randomUUID().toString();
        entity.expiresAt = LocalDateTime.now().plusDays(7);
        entity.used = false;
        entity.role = role;
        return entity;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markUsed() {
        this.used = true;
    }
}
