# Q&A 게시판 API

이 프로젝트는 파일 첨부, 댓글, 답글 기능을 갖춘 질문과 답변 게시판을 위한 RESTful API입니다.

## 기능

- 질문과 답변 게시글 생성, 조회, 수정, 삭제
- 다중 파일 업로드 및 다운로드
- 게시글에 댓글 추가, 수정, 삭제
- 댓글에 답글 추가, 수정, 삭제
- 웹 에디터를 통한 콘텐츠 서식 지원

## 기술 스택

- Spring Boot 3.5.3
- MyBatis (데이터베이스 접근용)
- MariaDB (데이터베이스)
- Spring Security (개발 환경에서는 비활성화)

## 설치 및 설정

### 필수 요구사항

- Java 24 이상
- MariaDB 10.x 이상
- Gradle 8.x 이상

### 데이터베이스 설정

1. `board_db`라는 이름의 MariaDB 데이터베이스 생성:
   ```sql
   CREATE DATABASE board_db;
   ```

2. `src/main/resources/application.properties`에서 데이터베이스 연결 설정:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/board_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. 애플리케이션은 시작 시 `src/main/resources/schema.sql`에 정의된 스키마를 사용하여 필요한 테이블을 자동으로 생성합니다.

### 애플리케이션 실행

1. 저장소 복제
2. 프로젝트 디렉토리로 이동
3. 애플리케이션 실행:
   ```bash
   ./gradlew bootRun
   ```
4. API는 `http://localhost:8080`에서 사용 가능합니다

## API 엔드포인트

### 게시판 엔드포인트

- `POST /api/boards` - 선택적 파일 첨부가 있는 새 게시글 생성
- `GET /api/boards` - 페이지네이션이 적용된 모든 게시글 조회
- `GET /api/boards/{boardId}` - ID로 특정 게시글 조회
- `PUT /api/boards/{boardId}` - 게시글 수정
- `DELETE /api/boards/{boardId}` - 게시글 삭제

### 댓글 엔드포인트

- `POST /api/comments` - 새 댓글 생성
- `GET /api/comments/board/{boardId}` - 특정 게시글의 모든 댓글 조회
- `GET /api/comments/{commentId}` - ID로 특정 댓글 조회
- `PUT /api/comments/{commentId}` - 댓글 수정
- `DELETE /api/comments/{commentId}` - 댓글 삭제

### 답글 엔드포인트

- `POST /api/replies` - 댓글에 새 답글 생성
- `GET /api/replies/comment/{commentId}` - 특정 댓글의 모든 답글 조회
- `GET /api/replies/{replyId}` - ID로 특정 답글 조회
- `PUT /api/replies/{replyId}` - 답글 수정
- `DELETE /api/replies/{replyId}` - 답글 삭제

### 파일 엔드포인트

- `GET /api/files/board/{boardId}` - 특정 게시글의 모든 파일 조회
- `GET /api/files/{fileId}` - 특정 파일 다운로드
- `DELETE /api/files/{fileId}` - 파일 삭제

## 파일 저장

파일은 애플리케이션 루트의 `files` 디렉토리에 저장됩니다. 디렉토리가 존재하지 않는 경우 자동으로 생성됩니다.

## 웹 에디터

이 애플리케이션은 콘텐츠 서식을 위한 웹 에디터를 지원합니다. 콘텐츠는 HTML 형식으로 데이터베이스에 저장됩니다.

## 보안

Spring Security는 현재 개발 목적으로 비활성화되어 있습니다. 프로덕션 환경에서는 인증 및 권한 부여를 활성화해야 합니다.
