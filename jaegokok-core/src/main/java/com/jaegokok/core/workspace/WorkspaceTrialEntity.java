package com.jaegokok.core.workspace;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_trials")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceTrialEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id", nullable = false, unique = true)
    private Long workspaceId;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    public static WorkspaceTrialEntity start(Long workspaceId) {
        WorkspaceTrialEntity entity = new WorkspaceTrialEntity();
        entity.workspaceId = workspaceId;
        entity.startedAt = LocalDateTime.now();
        entity.endsAt = LocalDateTime.now().plusDays(14);
        return entity;
    }

    public boolean isActive() {
        return LocalDateTime.now().isBefore(endsAt);
    }
}
