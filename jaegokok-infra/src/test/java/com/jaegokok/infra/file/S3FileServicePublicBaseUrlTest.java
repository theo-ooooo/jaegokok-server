package com.jaegokok.infra.file;

import com.jaegokok.infra.config.S3Properties;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class S3FileServicePublicBaseUrlTest {

    private final S3Client s3Client = mock(S3Client.class);

    private S3FileService service(String publicBaseUrl) {
        return new S3FileService(s3Client, new S3Properties("jaegokok-dev", "ap-northeast-2", publicBaseUrl));
    }

    @Test
    void fallsBackToS3WhenBaseUrlBlank() {
        String url = service("").toUrl("products/1/uuid.jpg");
        assertThat(url).isEqualTo("https://jaegokok-dev.s3.ap-northeast-2.amazonaws.com/products/1/uuid.jpg");
    }

    @Test
    void usesPublicBaseUrlWhenSet() {
        String url = service("https://cdn.jaegokok.com").toUrl("products/1/uuid.jpg");
        assertThat(url).isEqualTo("https://cdn.jaegokok.com/products/1/uuid.jpg");
    }

    @Test
    void stripsTrailingSlashes() {
        String url = service("https://cdn.jaegokok.com///").toUrl("products/1/uuid.jpg");
        assertThat(url).isEqualTo("https://cdn.jaegokok.com/products/1/uuid.jpg");
    }

    @Test
    void stripsLeadingSlashFromKey() {
        String url = service("https://cdn.jaegokok.com").toUrl("/products/1/uuid.jpg");
        assertThat(url).isEqualTo("https://cdn.jaegokok.com/products/1/uuid.jpg");
    }

    @Test
    void validatesSchemeOnStartup() {
        assertThatThrownBy(() -> service("cdn.jaegokok.com").validatePublicBaseUrl())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("S3_PUBLIC_BASE_URL must start with http:// or https://");
    }

    @Test
    void allowsBlankPublicBaseUrlOnStartup() {
        service("").validatePublicBaseUrl();
        service(null).validatePublicBaseUrl();
    }
}
