package com.hia.common.response;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        Object message,
        String field,
        String traceId,
        LocalDateTime timestamp
) {
    public static ErrorResponse of (HttpStatusCode statusCode, Object message){
        return new ErrorResponse(statusCode.value(),
                HttpStatus.valueOf(statusCode.value()).name(),
                message,
                null,
                MDC.get("traceId"),
                LocalDateTime.now());
    }

    public static ErrorResponse of (HttpStatusCode statusCode, String field, Object message){
        return new ErrorResponse(
                statusCode.value(),
                HttpStatus.valueOf(statusCode.value()).name(),
                message,
                field,
                MDC.get("traceId"),
                LocalDateTime.now()
        );
    }
}
