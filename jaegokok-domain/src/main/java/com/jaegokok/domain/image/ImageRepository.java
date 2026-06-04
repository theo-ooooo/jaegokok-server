package com.jaegokok.domain.image;

import com.jaegokok.core.image.ImageEntityType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ImageRepository {
    Image save(ImageEntityType entityType, Long entityId, String originalPath, String webpPath, String bucket);
    List<Image> findByEntity(ImageEntityType entityType, Long entityId);
    Optional<Image> findFirstByEntity(ImageEntityType entityType, Long entityId);
    void deleteByEntity(ImageEntityType entityType, Long entityId);
    Map<Long, Image> findFirstByEntityIds(ImageEntityType entityType, List<Long> entityIds);
}
