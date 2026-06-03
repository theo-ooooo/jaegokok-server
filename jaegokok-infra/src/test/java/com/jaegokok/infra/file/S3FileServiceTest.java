package com.jaegokok.infra.file;

import com.jaegokok.infra.config.S3Properties;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class S3FileServiceTest {

    private final S3Client s3Client = mock(S3Client.class);

    private S3FileService service(String bucket, String region) {
        return new S3FileService(s3Client, new S3Properties(bucket, region));
    }

    @Test
    void toUrl_returnsNullForNull() {
        assertThat(service("b", "ap-northeast-2").toUrl(null)).isNull();
    }

    @Test
    void toUrl_returnsNullForBlank() {
        assertThat(service("b", "ap-northeast-2").toUrl("   ")).isNull();
    }

    @Test
    void toUrl_assemblesVirtualHostedUrl() {
        String url = service("jaegokok-dev", "ap-northeast-2").toUrl("products/12/uuid.jpg");
        assertThat(url).isEqualTo("https://jaegokok-dev.s3.ap-northeast-2.amazonaws.com/products/12/uuid.jpg");
    }

    @Test
    void toUrl_passesThroughLegacyHttpsValue() {
        String legacy = "https://old-bucket.s3.amazonaws.com/products/1/old.jpg";
        assertThat(service("new", "ap-northeast-2").toUrl(legacy)).isEqualTo(legacy);
    }

    @Test
    void toUrl_passesThroughLegacyHttpValue() {
        String legacy = "http://old-bucket.s3.amazonaws.com/products/1/old.jpg";
        assertThat(service("new", "ap-northeast-2").toUrl(legacy)).isEqualTo(legacy);
    }
}
