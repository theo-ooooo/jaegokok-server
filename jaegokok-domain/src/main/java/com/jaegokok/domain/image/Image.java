package com.jaegokok.domain.image;

import com.jaegokok.core.image.ImageEntityType;
import java.time.LocalDateTime;

public record Image(
        Long id,
        ImageEntityType entityType,
        Long entityId,
        String originalPath,
        String webpPath,
        String bucket,
        LocalDateTime createdAt
) {}
