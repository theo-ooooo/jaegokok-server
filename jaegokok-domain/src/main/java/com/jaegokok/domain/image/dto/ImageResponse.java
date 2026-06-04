package com.jaegokok.domain.image.dto;

import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.domain.image.Image;

public record ImageResponse(
        Long id,
        String originalUrl,
        String webpUrl
) {
    public static ImageResponse from(Image image, FileUploadPort urlResolver) {
        return new ImageResponse(
                image.id(),
                urlResolver.toUrl(image.originalPath()),
                urlResolver.toUrl(image.webpPath())
        );
    }
}
