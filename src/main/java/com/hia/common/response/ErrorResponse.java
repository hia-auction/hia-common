package com.hia.common.response;

import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        Object message,
        String field,
        LocalDateTime timestamp
) {

    public static ErrorResponse of(
            HttpStatusCode status,
            String code,
            Object message
    ){
        return new ErrorResponse(
                status.value(),
                code,
                message,
                null,
                LocalDateTime.now()
        );
    }

    public static ErrorResponse of(
            HttpStatusCode status,
            String code,
            String field,
            Object message
    ){
        return new ErrorResponse(
                status.value(),
                code,
                message,
                field,
                LocalDateTime.now()
        );
    }
}
