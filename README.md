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
artifactId : hia-common
version    : 0.0.1-SNAPSHOT
```

그리고 서비스에서 Maven Local을 사용하는 경우:

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

## Publish to GitHub Packages

공통모듈은 GitHub Packages의 Maven Registry에도 배포합니다.

Repository:

```text
hia-auction/hia-common
```
### 서비스에서 사용
서비스의 build.gradle에 GitHub Packages 저장소를 추가합니다.

```gradle
def githubUsername =
        System.getenv('GITHUB_ACTOR')
        ?: findProperty('GitHubPackagesUsername')

def githubToken =
        System.getenv('GITHUB_TOKEN')
        ?: findProperty('GitHubPackagesPassword')

repositories {
    mavenCentral()

    maven {
        name = 'GitHubPackages'
        url = uri('https://maven.pkg.github.com/hia-auction/hia-common')

        credentials {
            username = githubUsername
            password = githubToken
        }
    }
}

dependencies {
    implementation 'com.hia:hia-common:0.0.1-SNAPSHOT'
}
```

### 로컬인증
로컬에서는 Gradle 사용자 홈의 gradle.properties에 GitHub Packages 인증 정보를 설정합니다.
```properties
GitHubPackagesUsername=YOUR_GITHUB_USERNAME
GitHubPackagesPassword=YOUR_PERSONAL_ACCESS_TOKEN
```
### GitHub Actions 인증
GitHub Actions에서는 GITHUB_TOKEN을 사용합니다.
```yaml
propertiespermissions:
  contents: read
  packages: read
```
빌드 단계 예시:
```yaml
- name: Build and Test
  run: ./gradlew clean build
  env:
    GITHUB_ACTOR: ${{ github.actor }}
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```
---

## Logging Policy
예상 가능한 요청/비즈니스 오류는 `WARN`으로 기록합니다.
예상하지 못한 서버 오류는 `ERROR`로 기록하며 원인 추적을 위해 stack trace를 남깁니다.
요청 본문 전체나 민감정보는 로그에 직접 기록하지 않습니다.


## Status

현재 다음 작업을 완료했습니다.

- 공통 응답 및 예외 처리 구현
- 주요 Spring MVC 예외 처리
- 단위 테스트
- Maven Local 배포
- GitHub Packages 배포
- User Service 공통모듈 연동
- ApiResponse 통합 검증
- CustomException / GlobalExceptionHandler 통합 검증
- GitHub Actions 환경에서 패키지 다운로드 및 빌드 검증
- JPA Auditing용 BaseTime 추가

향후 공통모듈은 실제 서비스 개발 과정에서 필요한 공통 기능을 점진적으로 확장할 예정입니다.

## JPA Auditing

공통모듈은 Entity의 생성/수정 시간을 관리하기 위한 `BaseTime`을 제공합니다.

### 사용 방법

JPA Entity에서 `BaseTime`을 상속합니다.

```java
@Entity
public class User extends BaseTime {
    // ...
}