package com.jaegokok.common;

import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.response.ErrorResponse;
import com.jaegokok.common.response.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<GlobalResponse<ErrorResponse>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(GlobalResponse.fail(
                        errorCode.getHttpStatus().value(),
                        ErrorResponse.of(errorCode.name(), errorCode.getMessage())
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<ErrorResponse>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.BAD_REQUEST.getMessage());
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(GlobalResponse.fail(
                        ErrorCode.BAD_REQUEST.getHttpStatus().value(),
                        ErrorResponse.of(ErrorCode.BAD_REQUEST.name(), message)
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<GlobalResponse<ErrorResponse>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus())
                .body(GlobalResponse.fail(
                        ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus().value(),
                        ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED.name(), ErrorCode.METHOD_NOT_ALLOWED.getMessage())
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<ErrorResponse>> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(GlobalResponse.fail(
                        ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value(),
                        ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR.name(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                ));
    }
}
