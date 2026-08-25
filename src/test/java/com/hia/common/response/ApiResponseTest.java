package com.hia.common.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ApiResponseTest {

    @Test
    void 데이터를_포함한_성공_응답을_생성한다(){
        //given = 테스트를 위한 준비
        String data = "test data";

        //when = 실제 테스트 행동
        ApiResponse<String> response = ApiResponse.success(data);

        //then = 기대한 결과, 검증 부분
        Assertions.assertTrue(response.success());
        Assertions.assertEquals("요청이 성공적으로 처리되었습니다.", response.message());
        Assertions.assertEquals(data, response.data());
    }

    @Test
    void 원하는_메시지로_성공_응답을_생성(){
        String message = "회원가입이 완료되었습니다.";
        String data = "user";

        ApiResponse<String> response = ApiResponse.success(message, data);

        Assertions.assertTrue(response.success());
        Assertions.assertEquals(message, response.message());
        Assertions.assertEquals(data, response.data());
    }

    @Test
    void 데이터가_없는_성공응답을_생성(){
        ApiResponse<Void> response = ApiResponse.successWithoutData();

        Assertions.assertTrue(response.success());
        Assertions.assertEquals("요청이 성공적으로 처리되었습니다.", response.message());
        Assertions.assertNull(response.data());
    }
}
