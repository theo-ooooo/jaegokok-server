package com.jaegokok.domain.image.dto;

import com.jaegokok.domain.image.Image;

public record ImageResponse(
        Long id,
        String originalPath,
        String webpPath
) {
    public static ImageResponse from(Image image) {
        return new ImageResponse(image.id(), image.originalPath(), image.webpPath());
    }
}
