package com.jaegokok.domain.product;

import java.time.LocalDateTime;

public record ProductImage(
        Long id,
        Long productId,
        String originalPath,
        String webpPath,
        String bucket,
        LocalDateTime createdAt
) {}
