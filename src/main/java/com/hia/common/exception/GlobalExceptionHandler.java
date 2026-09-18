package com.hia.common.exception;

import com.hia.common.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            CustomException exception
    ){
        ErrorCode errorCode = exception.getErrorCode();

        log.warn("Custom exception occurred. code={}, message={}",
                errorCode.getCode(),
                errorCode.getMessage());

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        log.warn("Validation failed. errorCount={}",
                exception.getBindingResult().getErrorCount());

        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "올바르지 않은 값입니다.",
                        (existing, replacement) -> existing
                ));

        ErrorCode errorCode = CommonErrorCode.VALIDATION_ERROR;

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errors
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception
    ){
        log.warn("Constraint validation failed. errorCount={}",
                exception.getConstraintViolations().size());

        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        ErrorCode errorCode = CommonErrorCode.VALIDATION_ERROR;

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errors
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception
    ){
        log.warn(
                "Parameter type mismatch. name={}, requiredType={}",
                exception.getName(),
                exception.getRequiredType() != null
                        ? exception.getRequiredType().getSimpleName()
                        : "unknown"
        );

        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER_TYPE;

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception
    ){
        log.warn(
                "Missing request parameter. name={}, type={}",
                exception.getParameterName(),
                exception.getParameterType()
        );

        ErrorCode errorCode = CommonErrorCode.MISSING_REQUEST_PARAMETER;

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ){
        Throwable cause = exception.getMostSpecificCause();

        log.warn(
                "Invalid request body. causeType={}, cause={}",
                cause != null
                        ? cause.getClass().getSimpleName()
                        : exception.getClass().getSimpleName(),
                cause != null
                        ? cause.getMessage()
                        : exception.getMessage()
        );

        ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_BODY;

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception
    ){
        log.warn(
                "HTTP method not supported. method={}, supportedMethods={}",
                exception.getMethod(),
                exception.getSupportedHttpMethods()
                );

        ErrorCode errorCode = CommonErrorCode.METHOD_NOT_ALLOWED;

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception){

        log.error("Unhandled exception occurred", exception);

        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.of(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(response);
    }
}
