package com.jaegokok.infra.image;

import com.jaegokok.core.image.ImageEntity;
import com.jaegokok.core.image.ImageEntityType;
import com.jaegokok.domain.image.Image;
import com.jaegokok.domain.image.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepository {

    private final ImageJpaRepository imageJpaRepository;

    @Override
    public Image save(ImageEntityType entityType, Long entityId, String originalPath, String webpPath, String bucket) {
        return toImage(imageJpaRepository.save(ImageEntity.of(entityType, entityId, originalPath, webpPath, bucket)));
    }

    @Override
    public List<Image> findByEntity(ImageEntityType entityType, Long entityId) {
        return imageJpaRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(this::toImage).toList();
    }

    @Override
    public Optional<Image> findFirstByEntity(ImageEntityType entityType, Long entityId) {
        return imageJpaRepository.findFirstByEntityTypeAndEntityId(entityType, entityId).map(this::toImage);
    }

    @Override
    @Transactional
    public void deleteByEntity(ImageEntityType entityType, Long entityId) {
        imageJpaRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
    }

    private Image toImage(ImageEntity e) {
        return new Image(e.getId(), e.getEntityType(), e.getEntityId(), e.getOriginalPath(), e.getWebpPath(), e.getBucket(), e.getCreatedAt());
    }
}
