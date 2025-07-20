# SQL 로깅 가이드

이 문서는 SQL 쿼리 로깅을 개선하기 위해 프로젝트에 적용된 변경 사항을 설명합니다.

## 변경 사항 개요

1. P6Spy 라이브러리를 추가하여 SQL 쿼리 로깅 기능 강화
2. 줄바꿈과 들여쓰기를 사용하여 SQL 쿼리를 보기 좋게 포맷팅
3. 쿼리 실행 시간 및 파라미터 값 표시

## 구현 세부 사항

### 1. P6Spy 의존성 추가

`build.gradle` 파일에 P6Spy 의존성을 추가했습니다:

```gradle
// P6Spy for SQL query logging with formatting
implementation 'p6spy:p6spy:3.9.1'
```

### 2. 데이터베이스 연결 설정 변경

`application.properties` 파일에서 데이터베이스 드라이버와 URL을 P6Spy를 사용하도록 변경했습니다:

```properties
# Database configuration
# Using P6Spy for SQL query logging with formatting
spring.datasource.driver-class-name=com.p6spy.engine.spy.P6SpyDriver
spring.datasource.url=jdbc:p6spy:mariadb://localhost:3306/board_db
```

### 3. P6Spy 설정 파일 생성

`src/main/resources/spy.properties` 파일을 생성하여 P6Spy의 동작을 구성했습니다:

```properties
# P6Spy Configuration
appender=com.p6spy.engine.spy.appender.Slf4JLogger
logMessageFormat=org.zerock.board.config.P6SpyPrettySqlFormatter
multiline=true
driverlist=org.mariadb.jdbc.Driver,oracle.jdbc.OracleDriver
dateformat=yyyy-MM-dd HH:mm:ss
excludecategories=info,debug,result,resultset,batch
includeParameterValues=true
autoflush=true
reloadproperties=true
reloadpropertiesinterval=3600
useprefix=false
```

### 4. 커스텀 SQL 포맷터 구현

`P6SpyPrettySqlFormatter` 클래스를 구현하여 SQL 쿼리를 보기 좋게 포맷팅했습니다:

```java
package org.zerock.board.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
// ... 기타 import 문

public class P6SpyPrettySqlFormatter implements MessageFormattingStrategy {
    // SQL 쿼리 포맷팅 로직 구현
}
```

이 클래스는 다음과 같은 기능을 제공합니다:

- SQL 키워드(SELECT, FROM, WHERE 등)를 줄바꿈과 들여쓰기로 구분
- 컬럼 목록, 조건절 등을 줄바꿈으로 구분하여 가독성 향상
- 쿼리 실행 시간 및 연결 ID 표시
- 타임스탬프 포맷팅

## 로그 출력 예시

변경 전:
```
2025-07-17 22:04:12.345 DEBUG 12345 --- [main] o.z.b.r.BoardRepository.findById  : ==>  Preparing: SELECT * FROM board WHERE board_id = ?
2025-07-17 22:04:12.456 DEBUG 12345 --- [main] o.z.b.r.BoardRepository.findById  : ==> Parameters: 1(Long)
2025-07-17 22:04:12.567 DEBUG 12345 --- [main] o.z.b.r.BoardRepository.findById  : <==      Result: 1
```

변경 후:
```
2025-07-17 22:04:12.345 | 10ms | Connection ID: 1 | 
SELECT
  board_id,
  title,
  content,
  writer,
  view_count,
  created_date,
  modified_date
  FROM board
  WHERE board_id = 1
```

## 사용 방법

이 변경 사항은 자동으로 적용됩니다. 애플리케이션을 실행하면 로그에 포맷팅된 SQL 쿼리가 표시됩니다.

로그 레벨은 `application.properties`에서 다음과 같이 설정되어 있습니다:

```properties
logging.level.org.zerock.board=DEBUG
logging.level.org.mybatis=DEBUG
```

## 주의 사항

1. P6Spy는 개발 환경에서만 사용하는 것이 좋습니다. 프로덕션 환경에서는 성능에 영향을 줄 수 있습니다.
2. 대용량 데이터를 다루는 쿼리의 경우 로그가 매우 길어질 수 있으므로 주의하세요.
