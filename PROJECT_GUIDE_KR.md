# Q&A 게시판 API - 프로젝트 가이드

## 목차
- [소개](#소개)
- [기능](#기능)
- [기술 아키텍처](#기술-아키텍처)
- [시작하기](#시작하기)
  - [필수 요구사항](#필수-요구사항)
  - [설치](#설치)
  - [데이터베이스 설정](#데이터베이스-설정)
  - [구성](#구성)
- [API 사용 가이드](#api-사용-가이드)
  - [게시판 작업](#게시판-작업)
  - [댓글 작업](#댓글-작업)
  - [답글 작업](#답글-작업)
  - [파일 작업](#파일-작업)
- [데이터베이스 스키마](#데이터베이스-스키마)
- [프로젝트 구조](#프로젝트-구조)
- [테스트 실행](#테스트-실행)
- [실용적인 예제](#실용적인-예제)
  - [완전한 Q&A 스레드 생성](#완전한-qa-스레드-생성)
  - [파일 업로드 및 다운로드](#파일-업로드-및-다운로드)
  - [댓글 및 답글 관리](#댓글-및-답글-관리)
- [API 문서](#api-문서)
- [문제 해결](#문제-해결)
- [보안 고려사항](#보안-고려사항)
- [기여하기](#기여하기)

## 소개

Q&A 게시판 API는 질문과 답변 포럼이나 지식 공유 플랫폼을 위한 종합적인 RESTful API입니다. 게시물 관리, 파일 첨부, 댓글, 중첩된 답글 등 현대적인 Q&A 시스템에 필요한 모든 필수 기능을 제공합니다.

이 프로젝트는 Spring Boot로 구축되었으며 데이터베이스 작업에 MyBatis를 사용하여 견고하고 유연합니다. API는 웹 에디터 통합을 통한 풍부한 콘텐츠를 지원하고 Swagger UI를 통해 포괄적인 문서를 제공합니다.

## 기능

- **게시물 관리**: Q&A 게시물 생성, 읽기, 업데이트, 삭제
- **파일 첨부**: 게시물당 여러 파일 업로드 및 다운로드
- **댓글 시스템**: 게시물에 댓글 추가, 편집, 삭제
- **중첩된 답글**: 댓글에 대한 답글 지원
- **페이지네이션**: 대용량 데이터셋의 효율적인 검색
- **웹 에디터 지원**: 풍부한 콘텐츠 서식 기능
- **API 문서**: Swagger UI를 통한 대화형 API 문서
- **포괄적인 테스트**: 모든 컨트롤러에 대한 단위 테스트

## 기술 아키텍처

이 프로젝트는 계층화된 아키텍처 패턴을 따릅니다:

1. **컨트롤러 계층**: HTTP 요청 및 응답 처리
2. **서비스 계층**: 비즈니스 로직 포함
3. **리포지토리 계층**: 데이터 액세스 관리
4. **모델 계층**: 데이터 구조 정의

사용된 주요 기술:

- **Spring Boot 3.5.3**: 애플리케이션 프레임워크
- **MyBatis**: 데이터베이스 작업을 위한 SQL 매퍼 프레임워크
- **MariaDB**: 관계형 데이터베이스
- **Spring Security**: 보안 프레임워크 (개발 환경에서는 비활성화)
- **Swagger UI (SpringDoc OpenAPI)**: API 문서화
- **JUnit 5**: 테스트 프레임워크
- **Gradle**: 빌드 도구

## 시작하기

### 필수 요구사항

시작하기 전에 다음이 설치되어 있는지 확인하세요:

- Java 24 이상
- MariaDB 10.x 이상
- Gradle 8.x 이상
- Git (선택 사항, 저장소 복제용)

### 설치

1. 저장소 복제 (또는 소스 코드 다운로드):
   ```bash
   git clone https://github.com/yourusername/board-api.git
   cd board-api
   ```

2. 프로젝트 빌드:
   ```bash
   ./gradlew build
   ```

### 데이터베이스 설정

1. MariaDB 데이터베이스 생성:
   ```sql
   CREATE DATABASE board_db;
   ```

2. 애플리케이션은 시작 시 `src/main/resources/schema.sql`에 정의된 스키마를 사용하여 필요한 테이블을 자동으로 생성합니다.

### 구성

1. `src/main/resources/application.properties`에서 데이터베이스 연결 구성:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/board_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

2. 파일 업로드 설정 구성 (선택 사항):
   ```properties
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=50MB
   file.upload.directory=files
   ```

3. 애플리케이션 실행:
   ```bash
   ./gradlew bootRun
   ```

4. API는 `http://localhost:8080`에서 사용 가능합니다

## API 사용 가이드

### 게시판 작업

#### 새 게시물 생성

**요청:**
```bash
curl -X POST http://localhost:8080/api/boards \
  -H "Content-Type: multipart/form-data" \
  -F "board={\"title\":\"Spring Boot 사용 방법?\",\"content\":\"Spring Boot를 처음 사용해보는데 시작하는 방법이 필요합니다.\",\"writer\":\"초보자\"}" \
  -F "files=@/path/to/your/file.pdf"
```

**응답:**
```json
{
  "boardId": 1,
  "message": "게시글이 성공적으로 생성되었습니다"
}
```

#### 페이지네이션을 통한 모든 게시물 가져오기

**요청:**
```bash
curl -X GET "http://localhost:8080/api/boards?page=1&size=10"
```

**응답:**
```json
{
  "boards": [
    {
      "boardId": 2,
      "title": "Spring Security 질문",
      "content": "OAuth2를 어떻게 구현하나요?",
      "writer": "security_fan",
      "viewCount": 5,
      "createdDate": "2023-10-15T14:30:45",
      "modifiedDate": "2023-10-15T14:30:45"
    },
    {
      "boardId": 1,
      "title": "Spring Boot 사용 방법?",
      "content": "Spring Boot를 처음 사용해보는데 시작하는 방법이 필요합니다.",
      "writer": "초보자",
      "viewCount": 10,
      "createdDate": "2023-10-14T09:15:30",
      "modifiedDate": "2023-10-14T09:15:30"
    }
  ],
  "currentPage": 1,
  "totalItems": 2,
  "totalPages": 1
}
```

### 댓글 작업

#### 새 댓글 생성

**요청:**
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -d '{
    "boardId": 1,
    "content": "이 정보가 매우 유용합니다!",
    "writer": "감사한_사용자"
  }'
```

**응답:**
```json
{
  "commentId": 2,
  "message": "댓글이 성공적으로 생성되었습니다"
}
```

### 답글 작업

#### 새 답글 생성

**요청:**
```bash
curl -X POST http://localhost:8080/api/replies \
  -H "Content-Type: application/json" \
  -d '{
    "commentId": 2,
    "content": "저도 동의합니다, 매우 유용해요!",
    "writer": "다른_사용자"
  }'
```

**응답:**
```json
{
  "replyId": 2,
  "message": "답글이 성공적으로 생성되었습니다"
}
```

### 파일 작업

#### 파일 다운로드

**요청:**
```bash
curl -X GET http://localhost:8080/api/files/1 -O -J
```

이 명령은 원래 파일 이름으로 파일을 다운로드합니다.

## 데이터베이스 스키마

이 프로젝트는 다음과 같은 데이터베이스 스키마를 사용합니다:

### 게시판 테이블
```sql
CREATE TABLE board (
    board_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    view_count INT DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 파일 테이블
```sql
CREATE TABLE file (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE
);
```

### 댓글 테이블
```sql
CREATE TABLE comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE
);
```

### 답글 테이블
```sql
CREATE TABLE reply (
    reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE
);
```

## API 문서

API는 Swagger UI를 사용하여 문서화되어 있습니다. 애플리케이션이 실행되면 다음 주소에서 대화형 API 문서에 접근할 수 있습니다:

```
http://localhost:8080/swagger-ui.html
```

이를 통해 모든 API 엔드포인트를 탐색하고 테스트할 수 있는 포괄적인 인터페이스가 제공됩니다.

## 문제 해결

### 일반적인 문제 및 해결책

1. **데이터베이스 연결 문제**:
   - MariaDB가 실행 중인지 확인
   - `application.properties`에서 데이터베이스 자격 증명 확인
   - `board_db` 데이터베이스가 존재하는지 확인

2. **파일 업로드 문제**:
   - `files` 디렉토리가 존재하고 쓰기 가능한지 확인
   - `application.properties`에서 파일 크기 제한 확인
   - 콘텐츠 타입이 `multipart/form-data`로 설정되어 있는지 확인

3. **API 응답 오류**:
   - 400 Bad Request: 요청 형식 및 매개변수 확인
   - 404 Not Found: 리소스 ID가 존재하는지 확인
   - 500 Internal Server Error: 자세한 내용은 서버 로그 확인

### 로깅

더 자세한 로깅을 활성화하려면 `application.properties`에 다음을 추가하세요:

```properties
logging.level.org.zerock.board=DEBUG
logging.level.org.mybatis=DEBUG
```

## 보안 고려사항

현재 구현에서는 개발 목적으로 Spring Security가 비활성화되어 있습니다. 프로덕션 환경에서는 다음을 수행해야 합니다:

1. **인증 활성화**: Spring Security를 사용하여 사용자 인증 구현
2. **권한 부여 추가**: 사용자 역할에 따라 엔드포인트 접근 제한
3. **민감한 데이터 보안**: 데이터베이스의 민감한 데이터 암호화
4. **HTTPS 구현**: 모든 통신에 SSL/TLS 사용
5. **입력 유효성 검사 구현**: 주입 공격을 방지하기 위해 모든 사용자 입력 검증
6. **속도 제한 구현**: DoS 공격으로부터 보호
