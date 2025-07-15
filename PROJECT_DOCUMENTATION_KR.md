# 질문 답변 게시판 API 프로젝트 문서

## 프로젝트 개요
이 프로젝트는 질문과 답변 게시판을 위한 RESTful API를 제공합니다. 사용자는 게시글을 작성하고, 파일을 첨부하며, 댓글과 대댓글을 통해 소통할 수 있습니다. 웹 에디터를 지원하여 풍부한 콘텐츠 작성이 가능합니다.

## 기술 스택
- **Spring Boot 3.5.3**: 애플리케이션 프레임워크
- **MyBatis**: 데이터베이스 접근 기술
- **MariaDB**: 데이터베이스
- **Spring Security**: 보안 (개발 환경에서는 비활성화)
- **Swagger UI (SpringDoc OpenAPI)**: API 문서화
- **JUnit 5**: 테스트 프레임워크

## 프로젝트 구조
프로젝트는 다음과 같은 주요 패키지로 구성되어 있습니다:

### 1. 모델 (model)
데이터베이스 테이블과 매핑되는 엔티티 클래스들입니다.
- `Board`: 게시글 정보
- `FileAttachment`: 파일 첨부 정보
- `Comment`: 댓글 정보
- `Reply`: 대댓글 정보

### 2. 리포지토리 (repository)
데이터베이스 접근을 담당하는 인터페이스들입니다.
- `BoardRepository`: 게시글 CRUD 작업
- `FileAttachmentRepository`: 파일 첨부 CRUD 작업
- `CommentRepository`: 댓글 CRUD 작업
- `ReplyRepository`: 대댓글 CRUD 작업

### 3. 서비스 (service)
비즈니스 로직을 처리하는 클래스들입니다.
- `BoardService`: 게시글 관련 비즈니스 로직
- `CommentService`: 댓글 관련 비즈니스 로직
- `ReplyService`: 대댓글 관련 비즈니스 로직

### 4. 컨트롤러 (controller)
클라이언트 요청을 처리하는 REST API 엔드포인트들입니다.
- `BoardController`: 게시글 관련 API
- `FileController`: 파일 관련 API
- `CommentController`: 댓글 관련 API
- `ReplyController`: 대댓글 관련 API

### 5. DTO (Data Transfer Object)
API 응답을 위한 데이터 전송 객체들입니다.
- `BoardResponseDTO`: 게시글 작성/수정/삭제 응답
- `BoardListResponseDTO`: 게시글 목록 조회 응답
- `CommentResponseDTO`: 댓글 작성/수정/삭제 응답
- `ReplyResponseDTO`: 대댓글 작성/수정/삭제 응답

### 6. 설정 (config)
애플리케이션 설정 클래스들입니다.
- `SecurityConfig`: Spring Security 설정
- `WebConfig`: 웹 관련 설정 (CORS, 리소스 핸들러 등)
- `SwaggerConfig`: Swagger UI 설정

## 데이터베이스 구조
프로젝트는 다음과 같은 테이블 구조를 사용합니다:

### 1. board 테이블
게시글 정보를 저장합니다.
- `board_id`: 게시글 ID (기본 키)
- `title`: 제목
- `content`: 내용
- `writer`: 작성자
- `view_count`: 조회수
- `created_date`: 생성일
- `modified_date`: 수정일

### 2. file 테이블
파일 첨부 정보를 저장합니다.
- `file_id`: 파일 ID (기본 키)
- `board_id`: 게시글 ID (외래 키)
- `original_filename`: 원본 파일명
- `stored_filename`: 저장된 파일명
- `file_size`: 파일 크기
- `file_type`: 파일 타입
- `created_date`: 생성일

### 3. comment 테이블
댓글 정보를 저장합니다.
- `comment_id`: 댓글 ID (기본 키)
- `board_id`: 게시글 ID (외래 키)
- `content`: 내용
- `writer`: 작성자
- `created_date`: 생성일
- `modified_date`: 수정일

### 4. reply 테이블
대댓글 정보를 저장합니다.
- `reply_id`: 대댓글 ID (기본 키)
- `comment_id`: 댓글 ID (외래 키)
- `content`: 내용
- `writer`: 작성자
- `created_date`: 생성일
- `modified_date`: 수정일

## API 엔드포인트

### 게시글 API
- `POST /api/boards`: 새 게시글 작성
- `GET /api/boards`: 모든 게시글 조회 (페이지네이션)
- `GET /api/boards/{boardId}`: 특정 게시글 조회
- `PUT /api/boards/{boardId}`: 게시글 수정
- `DELETE /api/boards/{boardId}`: 게시글 삭제

### 파일 API
- `GET /api/files/board/{boardId}`: 특정 게시글의 모든 파일 조회
- `GET /api/files/{fileId}`: 파일 다운로드
- `DELETE /api/files/{fileId}`: 파일 삭제

### 댓글 API
- `POST /api/comments`: 새 댓글 작성
- `GET /api/comments/board/{boardId}`: 특정 게시글의 모든 댓글 조회
- `GET /api/comments/{commentId}`: 특정 댓글 조회
- `PUT /api/comments/{commentId}`: 댓글 수정
- `DELETE /api/comments/{commentId}`: 댓글 삭제

### 대댓글 API
- `POST /api/replies`: 새 대댓글 작성
- `GET /api/replies/comment/{commentId}`: 특정 댓글의 모든 대댓글 조회
- `GET /api/replies/{replyId}`: 특정 대댓글 조회
- `PUT /api/replies/{replyId}`: 대댓글 수정
- `DELETE /api/replies/{replyId}`: 대댓글 삭제

## 파일 저장 구조
파일은 서버의 루트 디렉토리에 있는 `files` 폴더에 저장됩니다. 파일명 충돌을 방지하기 위해 UUID를 사용하여 고유한 파일명을 생성합니다. 원본 파일명과 저장된 파일명은 데이터베이스에 함께 저장됩니다.

## 테스트 코드
모든 컨트롤러에 대한 단위 테스트가 구현되어 있습니다. 테스트는 다음과 같은 클래스로 구성되어 있습니다:
- `BoardControllerTest`: 게시글 컨트롤러 테스트
- `CommentControllerTest`: 댓글 컨트롤러 테스트
- `ReplyControllerTest`: 대댓글 컨트롤러 테스트
- `FileControllerTest`: 파일 컨트롤러 테스트

## API 문서화
Swagger UI를 통해 API 문서를 제공합니다. 애플리케이션 실행 후 다음 URL에서 접근할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```

## 설정 방법

### 데이터베이스 설정
`application.properties` 파일에서 데이터베이스 연결 정보를 설정할 수 있습니다:
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/board_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 파일 업로드 설정
파일 업로드 관련 설정은 다음과 같이 구성되어 있습니다:
```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
file.upload.directory=files
```

## 실행 방법
1. MariaDB 데이터베이스 생성: `board_db`
2. 프로젝트 루트 디렉토리에서 다음 명령 실행:
   ```
   ./gradlew bootRun
   ```
3. 애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 보안 고려사항
현재 개발 환경에서는 Spring Security가 비활성화되어 있습니다. 프로덕션 환경에서는 적절한 인증 및 권한 부여 메커니즘을 구현해야 합니다.
