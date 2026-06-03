package com.jaegokok.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP method 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    WITHDRAWN_MEMBER(HttpStatus.CONFLICT, "이미 탈퇴한 회원입니다."),

    // Auth
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호를 확인해주세요."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    // Workspace
    WORKSPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "워크스페이스를 찾을 수 없습니다."),
    WORKSPACE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 워크스페이스에 접근 권한이 없습니다."),
    WORKSPACE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 워크스페이스가 존재합니다."),
    WORKSPACE_MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 워크스페이스에 소속된 회원입니다."),
    CANNOT_REMOVE_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 삭제할 수 없습니다."),
    CANNOT_CHANGE_OWNER_ROLE(HttpStatus.BAD_REQUEST, "OWNER 역할은 변경하거나 부여할 수 없습니다."),

    // Invitation
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "초대를 찾을 수 없습니다."),
    INVITATION_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용된 초대입니다."),
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 초대입니다."),

    // Billing
    TRIAL_ALREADY_STARTED(HttpStatus.CONFLICT, "이미 무료 체험이 진행 중입니다."),
    ALREADY_ON_PAID_PLAN(HttpStatus.CONFLICT, "이미 유료 플랜을 사용 중입니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "플랜의 상품 등록 한도를 초과했습니다."),

    // Inventory
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),

    // File
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일(JPEG, PNG, WEBP)만 업로드 가능합니다."),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "파일 크기는 5MB를 초과할 수 없습니다."),
    IMAGE_CONVERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 변환에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() { return httpStatus; }
    public String getMessage() { return message; }
}
