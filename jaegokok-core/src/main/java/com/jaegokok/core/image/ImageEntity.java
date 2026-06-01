package com.jaegokok.core.image;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images", indexes = @Index(name = "idx_images_entity", columnList = "entity_type, entity_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private ImageEntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 500)
    private String originalPath;

    @Column(length = 500)
    private String webpPath;

    @Column(nullable = false, length = 100)
    private String bucket;

    @Builder
    private ImageEntity(ImageEntityType entityType, Long entityId, String originalPath, String webpPath, String bucket) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.originalPath = originalPath;
        this.webpPath = webpPath;
        this.bucket = bucket;
    }

    public static ImageEntity of(ImageEntityType entityType, Long entityId, String originalPath, String webpPath, String bucket) {
        return ImageEntity.builder()
                .entityType(entityType).entityId(entityId)
                .originalPath(originalPath).webpPath(webpPath).bucket(bucket)
                .build();
    }
}
