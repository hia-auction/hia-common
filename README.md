# HIA Common Library

HIA Auction 프로젝트의 서비스에서 공통으로 사용하는 기능을 제공하는 Java 라이브러리입니다.

현재 공통 응답 형식과 예외 처리 기능을 제공합니다.

## Environment

- Java 21
- Gradle
- Spring Framework 6.x
- Jakarta Validation
- SLF4J

## Features

### Common Response

- ApiResponse
- ErrorResponse

### Exception Handling

- ErrorCode
- CommonErrorCode
- CustomException
- GlobalExceptionHandler

현재 공통 처리하는 주요 예외:

- MethodArgumentNotValidException
- ConstraintViolationException
- HttpMessageNotReadableException
- MethodArgumentTypeMismatchException
- MissingServletRequestParameterException
- HttpRequestMethodNotSupportedException
- NoResourceFoundException
- Exception

### ErrorCode

각 서비스는 `ErrorCode` 인터페이스를 구현하여 서비스별 에러 코드를 정의할 수 있습니다.

예시:

```java
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND",
            "사용자를 찾을 수 없습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    UserErrorCode(
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
```

### CustomException

서비스에서 정의한 `ErrorCode`를 이용하여 비지니스 예외를 발생시킬 수 있습니다.

```java
throw new CustomException(UserErrorCode.USER_NOT_FOUND);
```

`GlobalExceptionHandler`는 `CustomException`이 가지고 있는 `ErrorCode`를 이용하여 
공통 `ErrorResponse`로 변환합니다.

### ErrorResponse
예시 :
```json
{
  "status": 404,
  "error": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다."
}
```
`Validation` 오류의 경우 `message`에 필드별 검증 결과가 포함될 수 있습니다.
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": {
    "email": "이메일 형식이 올바르지 않습니다.",
    "password": "비밀번호는 필수입니다."
  }
}
```
## Local Build
전체 테스트:
```
./gradlew clean test
```
## Publish to Maven Local
로컬 Maven 저장소에 배포:
```
./gradlew clean publishToMavenLocal
```
현재 Maven 좌표:
```
groupId    : com.hia
artifactId : common
version    : 0.0.1-SNAPSHOT
```

그리고 서비스 적용 예시는:

```gradle
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation "com.hia:common:0.0.1-SNAPSHOT"
}
```
실제 서비스 프로젝트 생성 후 의존성 적용 및 Spring Bean 등록 여부를 통합 검증할 예정입니다.

## Logging Policy
예상 가능한 요청/비즈니스 오류는 `WARN`으로 기록합니다.
예상하지 못한 서버 오류는 `ERROR`로 기록하며 원인 추적을 위해 stack trace를 남깁니다.
요청 본문 전체나 민감정보는 로그에 직접 기록하지 않습니다.
## Status

현재 공통모듈 자체 구현, 단위 테스트 및 Maven Local 배포까지 완료했습니다.

향후 실제 서비스 프로젝트에 적용하여 다음 항목을 검증할 예정입니다.

- 공통모듈 의존성 적용
- GlobalExceptionHandler Bean 등록
- 서비스별 ErrorCode 확장
- 실제 HTTP 요청/응답 검증
- 의존성 scope 최종 점검