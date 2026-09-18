package com.hia.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode{

    VALIDATION_ERROR(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "요청 값이 올바르지 않습니다."
    ),

    INVALID_REQUEST_BODY(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST_BODY",
            "요청 본문의 형식이 올바르지 않습니다."
    ),

    INVALID_PARAMETER_TYPE(
            HttpStatus.BAD_REQUEST,
            "INVALID_PARAMETER_TYPE",
            "요청 파라미터의 형식이 올바르지 않습니다."
    ),

    MISSING_REQUEST_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "MISSING_REQUEST_PARAMETER",
            "필수 요청 파라미터가 누락되었습니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다."
    ),

    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "METHOD_NOT_ALLOWED",
            "허용되지 않은 HTTP 요청 메서드입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    CommonErrorCode(
            HttpStatus status,
            String code,
            String message
    ) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
