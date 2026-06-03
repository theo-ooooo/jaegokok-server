package com.jaegokok.infra.file;

import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.infra.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService implements FileUploadPort {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public String upload(String directory, String originalFilename, byte[] content, String contentType) {
        String ext = extractExtension(originalFilename);
        String key = directory + "/" + UUID.randomUUID() + "." + ext;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(s3Properties.bucket())
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(content)
        );

        return key;
    }

    @Override
    public String getBucket() {
        return s3Properties.bucket();
    }

    @Override
    public String toUrl(String key) {
        if (key == null || key.isBlank()) return null;
        String base = s3Properties.publicBaseUrl();
        if (base != null && !base.isBlank()) {
            return base.replaceAll("/+$", "") + "/" + key;
        }
        return "https://" + s3Properties.bucket() + ".s3." + s3Properties.region() + ".amazonaws.com/" + key;
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx + 1).toLowerCase() : "bin";
    }
}
