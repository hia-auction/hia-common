package com.hia.common.exception;

import com.hia.common.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Set;

public class GlobalExceptionHandlerTest {

    private enum TestErrorCode implements ErrorCode{

        TEST_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "TEST_NOT_FOUND",
                "테스트 리소스를 찾을 수 없습니다."
        );

        private final HttpStatus status;
        private final String code;
        private final String message;

        TestErrorCode(HttpStatus status, String code, String message){
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

    @Test
    void CustomException_Handler사용하여_ErrorResponse로_변환(){
        //given 준비
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        CustomException exception = new CustomException(TestErrorCode.TEST_NOT_FOUND);

        //when 상황 또는 행동
        ResponseEntity<ErrorResponse> response =
                handler.handleCustomException(exception);

        //then 결과 검증
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Assertions.assertEquals(404, response.getBody().status());

        Assertions.assertEquals("TEST_NOT_FOUND", response.getBody().error());

        Assertions.assertEquals("테스트 리소스를 찾을 수 없습니다." , response.getBody().message());
    }

    @Test
    void 예상치_못한_Exception_Handler를_사용하여_ErrorResponse_변환(){

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Exception exception = new RuntimeException("가상의 내부 오류");

        ResponseEntity<ErrorResponse> response = handler.handleUnexpectedException(exception);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(500, response.getBody().status());

        Assertions.assertEquals("INTERNAL_SERVER_ERROR", response.getBody().error());

        Assertions.assertEquals("서버 내부 오류가 발생했습니다.", response.getBody().message());
    }

    @Test
    void MethodArgumentNotValidException을_ErrorResponse로_변환(){

        Object target = new Object();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testRequest");

        bindingResult.addError(
                new FieldError(
                        "testRequest",
                        "email",
                        "이메일 형식이 올바르지 않습니다."
                )
        );

        bindingResult.addError(
                new FieldError(
                        "testRequest",
                        "password",
                        "비밀번호는 필수입니다."
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValidException(exception);

        Assertions.assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(
                400,
                response.getBody().status()
        );

        Assertions.assertEquals(
                "VALIDATION_ERROR",
                response.getBody().error()
        );

        Map<String, String> errors =
                (Map<String, String>) response.getBody().message();

        Assertions.assertEquals(
                "이메일 형식이 올바르지 않습니다.",
                errors.get("email")
        );

        Assertions.assertEquals(
                "비밀번호는 필수입니다.",
                errors.get("password")
        );
    }

    @Test
    void ConstraintViolationException을_ErrorResponse로_반환(){

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);

        Path propertyPath = Mockito.mock(Path.class);

        Mockito.when(propertyPath.toString())
                .thenReturn("getUser.id");

        Mockito.when(violation.getPropertyPath())
                .thenReturn(propertyPath);

        Mockito.when(violation.getMessage())
                .thenReturn("0보다 커야 합니다.");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolationException(exception);

        // then
        Assertions.assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(
                400,
                response.getBody().status()
        );

        Assertions.assertEquals(
                "VALIDATION_ERROR",
                response.getBody().error()
        );

        Map<String, String> errors =
                (Map<String, String>) response.getBody().message();

        Assertions.assertEquals(
                "0보다 커야 합니다.",
                errors.get("getUser.id")
        );
    }

    @Test
    void HttpMessageNotReadableException을_ErrorResponse로_반환(){

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("가상의 요청 본문 파싱 오류");

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpMessageNotReadableException(exception);

        Assertions.assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(
                400,
                response.getBody().status()
        );

        Assertions.assertEquals(
                "INVALID_REQUEST_BODY",
                response.getBody().error()
        );

        Assertions.assertEquals(
                "요청 본문의 형식이 올바르지 않습니다.",
                response.getBody().message()
        );
    }

    @Test
    void MethodArgumentTypeMismatchException을_ErrorResponse로_반환(){

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        MethodParameter methodParameter = Mockito.mock(MethodParameter.class);

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "abc",
                Long.class,
                "id",
                methodParameter,
                new IllegalArgumentException("타입 변환 실패")
        );

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentTypeMismatchException(exception);

        Assertions.assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(
                400,
                response.getBody().status()
        );

        Assertions.assertEquals(
                "INVALID_PARAMETER_TYPE",
                response.getBody().error()
        );

        Assertions.assertEquals(
                "요청 파라미터의 형식이 올바르지 않습니다.",
                response.getBody().message()
        );
    }

    @Test
    void MissingServletRequestParameterException을_ErrorResponse로_반환(){

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("id", "Long");

        ResponseEntity<ErrorResponse> response = handler.handleMissingServletRequestParameterException(exception);

        Assertions.assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(
                400,
                response.getBody().status()
        );

        Assertions.assertEquals(
                "MISSING_REQUEST_PARAMETER",
                response.getBody().error()
        );

        Assertions.assertEquals(
                "필수 요청 파라미터가 누락되었습니다.",
                response.getBody().message()
        );
    }

    @Test
    void HttpRequestMethodNotSupportedException을_ErrorResponse로_반환(){

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpRequestMethodNotSupportedException(exception);

        Assertions.assertEquals(
                HttpStatus.METHOD_NOT_ALLOWED,
                response.getStatusCode()
        );

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(
                405,
                response.getBody().status()
        );

        Assertions.assertEquals(
                "METHOD_NOT_ALLOWED",
                response.getBody().error()
        );

        Assertions.assertEquals(
                "지원하지 않는 HTTP 메서드입니다.",
                response.getBody().message()
        );
    }

    @Test
    void NoResourceFoundException을_ErrorResponse로_반환(){

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.POST, "/users/999");

        ResponseEntity<ErrorResponse> response = handler.handleNoResourceFoundException(exception);

        Assertions.assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(
                404,
                response.getBody().status()
        );

        Assertions.assertEquals(
                "RESOURCE_NOT_FOUND",
                response.getBody().error()
        );

        Assertions.assertEquals(
                "요청한 리소스를 찾을 수 없습니다.",
                response.getBody().message()
        );
    }
}