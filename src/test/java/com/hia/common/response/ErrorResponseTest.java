package com.hia.common.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ErrorResponseTest {
    @Test
    void 필드가_없는_실패_응답_생성(){
        HttpStatus status = HttpStatus.NOT_FOUND;
        Object message = "리소스를 찾을 수 없습니다.";

        ErrorResponse response = ErrorResponse.of(status, message);

        Assertions.assertEquals(404, response.status());
        Assertions.assertEquals("NOT_FOUND", response.error());
        Assertions.assertEquals(message, response.message());
        Assertions.assertNull(response.field());
        Assertions.assertNotNull(response.timestamp());
    }

    @Test
    void 필드가_있는_실패_응답_생성(){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String field = "email";
        Object message = "이메일 형식이 올바르지 않습니다.";

        ErrorResponse response = ErrorResponse.of(status, field, message);

        Assertions.assertEquals(400, response.status());
        Assertions.assertEquals("BAD_REQUEST", response.error());
        Assertions.assertEquals(message, response.message());
        Assertions.assertEquals(field, response.field());
        Assertions.assertNotNull(response.timestamp());
    }

}
