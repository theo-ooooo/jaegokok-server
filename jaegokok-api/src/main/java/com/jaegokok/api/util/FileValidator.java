package com.jaegokok.api.util;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidator {

    private static final long MAX_SIZE = 5 * 1024 * 1024L;
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    private FileValidator() {}

    public static void validateImage(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new CustomException(ErrorCode.FILE_TOO_LARGE);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    public static String safeFilename(MultipartFile file) {
        String name = file.getOriginalFilename();
        return name != null ? name : "upload";
    }
}
