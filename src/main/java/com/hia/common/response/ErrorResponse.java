package com.hia.common.response;

import org.springframework.http.HttpStatus;
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
            Object message
    ){
        return new ErrorResponse(
                status.value(),
                HttpStatus.valueOf(status.value()).name(),
                message,
                null,
                LocalDateTime.now()
        );
    }

    public static ErrorResponse of(
            HttpStatusCode status,
            String field,
            Object message
    ){
        return new ErrorResponse(
                status.value(),
                HttpStatus.valueOf(status.value()).name(),
                message,
                field,
                LocalDateTime.now()
        );
    }
}
