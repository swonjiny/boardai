# 데이터베이스 연결 가이드

이 문서는 Q&A 게시판 애플리케이션의 데이터베이스 연결 설정 및 문제 해결에 대한 정보를 제공합니다.

## 이중 데이터베이스 지원

이 애플리케이션은 MariaDB와 Oracle 데이터베이스를 모두 지원합니다. 기본적으로 MariaDB를 사용하지만, 데이터베이스 API 엔드포인트를 사용하여 런타임에 Oracle로 전환할 수 있습니다.

### 작동 방식

1. **설정**: 두 데이터베이스 연결 모두 `application.properties`에 구성됩니다.
2. **동적 라우팅**: 애플리케이션은 Spring의 AbstractRoutingDataSource를 사용하여 데이터베이스 작업을 선택된 데이터베이스로 동적으로 라우팅합니다.
3. **지연 로딩**: Oracle 데이터베이스 연결은 실제로 필요할 때까지 초기화되지 않습니다.
4. **데이터베이스별 SQL**: MyBatis는 필요한 경우(예: 페이지네이션) 데이터베이스별 SQL 문을 사용하도록 구성됩니다.
5. **스키마 초기화**: 선택된 데이터베이스 유형에 따라 적절한 스키마 파일이 로드됩니다.
6. **API 엔드포인트**: 애플리케이션은 현재 데이터베이스 유형을 확인하고 전환하기 위한 API 엔드포인트를 제공합니다.

## 데이터베이스 설정

### MariaDB 설정 (기본값)

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

### Oracle 설정 (선택 사항)

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

## 데이터베이스 전환

애플리케이션이 실행 중일 때 데이터베이스를 전환하려면 다음 API 엔드포인트를 사용하세요:

```
POST /api/database/switch?databaseType=ORACLE
```

또는

```
POST /api/database/switch?databaseType=MARIADB
```

현재 데이터베이스 유형을 확인하려면 다음 API 엔드포인트를 사용하세요:

```
GET /api/database/type
```

## 문제 해결

### Oracle 연결 오류

애플리케이션은 Oracle 데이터베이스가 사용 가능하지 않더라도 MariaDB로 시작할 수 있습니다. Oracle 데이터베이스 연결은 실제로 Oracle로 전환할 때까지 초기화되지 않습니다.

Oracle로 전환할 때 연결 오류가 발생하면 다음을 확인하세요:

1. Oracle 데이터베이스가 실행 중인지 확인
2. `application.properties`의 Oracle 연결 설정이 올바른지 확인
3. Oracle 사용자에게 적절한 권한이 있는지 확인

### 스키마 초기화 오류

애플리케이션은 시작 시 선택된 데이터베이스 유형에 따라 적절한 스키마 파일을 로드합니다. 스키마 초기화 오류가 발생하면 다음을 확인하세요:

1. 데이터베이스가 실행 중인지 확인
2. 데이터베이스 사용자에게 테이블을 생성할 권한이 있는지 확인
3. 스키마 파일(`schema.sql` 또는 `schema-oracle.sql`)이 올바른지 확인

## 기술적 구현 세부 사항

애플리케이션은 다음과 같은 기술을 사용하여 이중 데이터베이스 지원을 구현합니다:

1. **AbstractRoutingDataSource**: 런타임에 데이터베이스를 전환할 수 있는 동적 라우팅 데이터 소스
2. **LazyConnectionDataSourceProxy**: 실제로 필요할 때까지 데이터베이스 연결을 초기화하지 않는 지연 로딩 프록시
3. **@Lazy 어노테이션**: Spring이 필요할 때까지 빈을 초기화하지 않도록 지시
4. **DatabaseIdProvider**: MyBatis가 현재 데이터베이스 유형에 따라 적절한 SQL 문을 선택할 수 있도록 함

이러한 기술을 조합하여 애플리케이션은 MariaDB로 시작하고 필요한 경우에만 Oracle에 연결할 수 있습니다.
