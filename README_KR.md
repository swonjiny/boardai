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
- 이중 데이터베이스 지원 (MariaDB 및 Oracle)
- Spring Security (개발 환경에서는 비활성화)

## 설치 및 설정

### 필수 요구사항

- Java 24 이상
- MariaDB 10.x 이상
- Gradle 8.x 이상

### 데이터베이스 설정

#### MariaDB 설정 (기본값)

1. `board_db`라는 이름의 MariaDB 데이터베이스 생성:
   ```sql
   CREATE DATABASE board_db;
   ```

2. `src/main/resources/application.properties`에서 MariaDB 연결 설정:
   ```properties
   spring.database.type=mariadb
   spring.datasource.mariadb.jdbc-url=jdbc:mariadb://localhost:3306/board_db
   spring.datasource.mariadb.username=your_username
   spring.datasource.mariadb.password=your_password
   ```

#### Oracle 설정 (선택 사항)

1. Oracle 사용자 및 테이블스페이스 생성:
   ```sql
   CREATE USER board_user IDENTIFIED BY board_password;
   GRANT CONNECT, RESOURCE TO board_user;
   ALTER USER board_user QUOTA UNLIMITED ON USERS;
   ```

2. `src/main/resources/application.properties`에서 Oracle 연결 설정:
   ```properties
   spring.datasource.oracle.jdbc-url=jdbc:oracle:thin:@localhost:1521:XE
   spring.datasource.oracle.username=board_user
   spring.datasource.oracle.password=board_password
   ```

3. 애플리케이션은 시작 시 선택된 데이터베이스 유형에 따라 적절한 스키마 파일을 사용하여 필요한 테이블을 자동으로 생성합니다:
   - MariaDB: `src/main/resources/schema.sql`
   - Oracle: `src/main/resources/schema-oracle.sql`

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

### 데이터베이스 엔드포인트

- `GET /api/database/type` - 현재 데이터베이스 유형 조회 (MARIADB 또는 ORACLE)
- `POST /api/database/switch?databaseType=MARIADB|ORACLE` - 지정된 데이터베이스 유형으로 전환

## 파일 저장

파일은 애플리케이션 루트의 `files` 디렉토리에 저장됩니다. 디렉토리가 존재하지 않는 경우 자동으로 생성됩니다.

## 웹 에디터

이 애플리케이션은 콘텐츠 서식을 위한 웹 에디터를 지원합니다. 콘텐츠는 HTML 형식으로 데이터베이스에 저장되며 base64로 인코딩된 데이터 URL 형태의 blob 이미지를 포함할 수 있습니다. 이를 통해 별도의 파일 업로드 없이 인라인 이미지가 포함된 풍부한 콘텐츠를 제공할 수 있습니다.

## 이중 데이터베이스 지원

이 애플리케이션은 MariaDB와 Oracle 데이터베이스를 모두 지원합니다. 기본적으로 MariaDB를 사용하지만, 데이터베이스 API 엔드포인트를 사용하여 런타임에 Oracle로 전환할 수 있습니다.

### 작동 방식

1. **설정**: 두 데이터베이스 연결 모두 `application.properties`에 구성됩니다.
2. **동적 라우팅**: 애플리케이션은 Spring의 AbstractRoutingDataSource를 사용하여 데이터베이스 작업을 선택된 데이터베이스로 동적으로 라우팅합니다.
3. **데이터베이스별 SQL**: MyBatis는 필요한 경우(예: 페이지네이션) 데이터베이스별 SQL 문을 사용하도록 구성됩니다.
4. **스키마 초기화**: 선택된 데이터베이스 유형에 따라 적절한 스키마 파일이 로드됩니다.
5. **API 엔드포인트**: 애플리케이션은 현재 데이터베이스 유형을 확인하고 전환하기 위한 API 엔드포인트를 제공합니다.

### 사용 사례

- **개발 및 테스트**: 코드를 변경하지 않고 다양한 데이터베이스 시스템에 대해 애플리케이션을 테스트할 수 있습니다.
- **마이그레이션**: 한 데이터베이스 시스템에서 다른 시스템으로 점진적으로 마이그레이션할 수 있습니다.
- **멀티테넌트 배포**: 다양한 배포에 대해 다양한 데이터베이스 시스템을 지원할 수 있습니다.

## 보안

Spring Security는 현재 개발 목적으로 비활성화되어 있습니다. 프로덕션 환경에서는 인증 및 권한 부여를 활성화해야 합니다.
