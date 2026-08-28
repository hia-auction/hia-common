package com.hia.common.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class CustomExceptionTest {

    private enum TestErrorCode implements ErrorCode {
        TEST_ERROR(
                HttpStatus.BAD_REQUEST,
                "TEST_ERROR",
                "테스트 에러입니다."
        );

        private final HttpStatus status;
        private final String code;
        private final String message;

        TestErrorCode(HttpStatus status,String code, String message){
            this.status = status;
            this.code = code;
            this.message = message;
        }

        @Override
        public HttpStatus getStatus(){
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
    void ErrorCode로_CustomException생성(){

        ErrorCode errorCode = TestErrorCode.TEST_ERROR;

        CustomException exception = new CustomException(errorCode);

        Assertions.assertEquals(errorCode, exception.getErrorCode());
        Assertions.assertEquals("테스트 에러입니다.", exception.getMessage());
    }
}
