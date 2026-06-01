package com.jaegokok.core.workspace;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workspace_logos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceLogoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @Column(nullable = false, length = 500)
    private String originalPath;

    @Column(length = 500)
    private String webpPath;

    @Column(nullable = false, length = 100)
    private String bucket;

    @Builder
    private WorkspaceLogoEntity(WorkspaceEntity workspace, String originalPath, String webpPath, String bucket) {
        this.workspace = workspace;
        this.originalPath = originalPath;
        this.webpPath = webpPath;
        this.bucket = bucket;
    }

    public static WorkspaceLogoEntity of(WorkspaceEntity workspace, String originalPath, String webpPath, String bucket) {
        return WorkspaceLogoEntity.builder()
                .workspace(workspace)
                .originalPath(originalPath)
                .webpPath(webpPath)
                .bucket(bucket)
                .build();
    }
}
