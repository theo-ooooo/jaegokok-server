package com.jaegokok.common.response;

import java.time.LocalDateTime;

public record GlobalResponse<T>(
        boolean isSuccess,
        int status,
        T data,
        LocalDateTime timestamp
) {
    public static <T> GlobalResponse<T> success(int status, T data) {
        return new GlobalResponse<>(true, status, data, LocalDateTime.now());
    }

    public static GlobalResponse<ErrorResponse> fail(int status, ErrorResponse errorResponse) {
        return new GlobalResponse<>(false, status, errorResponse, LocalDateTime.now());
    }
}
