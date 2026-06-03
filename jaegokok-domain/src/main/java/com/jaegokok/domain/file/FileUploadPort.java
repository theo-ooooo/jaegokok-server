package com.jaegokok.domain.file;

public interface FileUploadPort {
    String upload(String directory, String originalFilename, byte[] content, String contentType);
    String getBucket();
    String toUrl(String key);
}
