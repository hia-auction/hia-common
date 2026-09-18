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

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다."
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
