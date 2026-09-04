package com.hia.common.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ErrorResponseTest {
    @Test
    void 필드가_없는_실패_응답_생성(){
        HttpStatus status = HttpStatus.NOT_FOUND;
        String code = "USER_NOT_FOUND";
        Object message = "리소스를 찾을 수 없습니다.";

        ErrorResponse response = ErrorResponse.of(status, code, message);

        Assertions.assertEquals(404, response.status());
        Assertions.assertEquals(code, response.error());
        Assertions.assertEquals(message, response.message());
        Assertions.assertNull(response.field());
        Assertions.assertNotNull(response.timestamp());
    }

    @Test
    void 필드가_있는_실패_응답_생성(){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String field = "email";
        String code = "INVALID_EMAIL";
        Object message = "이메일 형식이 올바르지 않습니다.";

        ErrorResponse response = ErrorResponse.of(status, code, field, message);

        Assertions.assertEquals(400, response.status());
        Assertions.assertEquals(code , response.error());
        Assertions.assertEquals(message, response.message());
        Assertions.assertEquals(field, response.field());
        Assertions.assertNotNull(response.timestamp());
    }

}
