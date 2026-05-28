package com.jaegokok.common.response;

public record ErrorResponse(String errorName, String message) {

    public static ErrorResponse of(String errorName, String message) {
        return new ErrorResponse(errorName, message);
    }
}
