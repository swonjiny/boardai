# 중첩 댓글 테스트 가이드 (Nested Comments Test Guide)

이 문서는 다중 레벨 중첩 댓글 기능을 테스트하는 방법을 설명합니다.

## 테스트 준비

1. 애플리케이션이 실행 중인지 확인합니다.
2. 테스트용 게시글이 있는지 확인합니다. 없다면 새 게시글을 생성합니다.
3. API 요청을 보낼 수 있는 도구(Postman, curl, 또는 웹 브라우저의 개발자 도구)를 준비합니다.

## 테스트 시나리오

### 테스트 1: 다중 레벨 중첩 댓글 생성

이 테스트는 게시글에 댓글을 작성하고, 그 댓글에 대댓글을 작성하고, 대댓글에 또 댓글을 작성하는 과정을 검증합니다.

#### 1. 최상위 댓글 생성 (Level 1)

```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -d '{
    "boardId": 1,
    "content": "안녕하세요, 좋은 게시글입니다.",
    "writer": "user1"
  }'
```

응답에서 `commentId`를 기록해 둡니다. 예: `101`

#### 2. 두 번째 레벨 댓글 생성 (Level 2)

```bash
curl -X POST http://localhost:8080/api/comments/nested \
  -H "Content-Type: application/json" \
  -d '{
    "boardId": 1,
    "parentCommentId": 101,
    "content": "감사합니다!",
    "writer": "author"
  }'
```

응답에서 `commentId`를 기록해 둡니다. 예: `103`

#### 3. 세 번째 레벨 댓글 생성 (Level 3)

```bash
curl -X POST http://localhost:8080/api/comments/nested \
  -H "Content-Type: application/json" \
  -d '{
    "boardId": 1,
    "parentCommentId": 103,
    "content": "저도 정말 좋은 게시글이라고 생각합니다.",
    "writer": "user4"
  }'
```

응답에서 `commentId`를 기록해 둡니다. 예: `106`

#### 4. 네 번째 레벨 댓글 생성 (Level 4)

```bash
curl -X POST http://localhost:8080/api/comments/nested \
  -H "Content-Type: application/json" \
  -d '{
    "boardId": 1,
    "parentCommentId": 106,
    "content": "동의합니다!",
    "writer": "user6"
  }'
```

### 테스트 2: 계층형 댓글 조회

#### 1. 게시글의 모든 댓글을 계층형 구조로 조회

```bash
curl -X GET http://localhost:8080/api/comments/board/1/nested
```

응답이 계층형 구조로 반환되는지 확인합니다. 최상위 댓글부터 시작하여 각 댓글의 `children` 배열에 하위 댓글이 포함되어 있어야 합니다.

#### 2. 특정 댓글과 그 하위 댓글 조회

```bash
curl -X GET http://localhost:8080/api/comments/101
```

응답에 댓글 101과 그 하위의 모든 댓글이 계층형 구조로 포함되어 있는지 확인합니다.

### 테스트 3: 중첩 댓글 삭제

#### 1. 중간 레벨의 댓글 삭제

```bash
curl -X DELETE http://localhost:8080/api/comments/103
```

#### 2. 삭제 후 계층형 댓글 조회

```bash
curl -X GET http://localhost:8080/api/comments/board/1/nested
```

댓글 103과 그 하위의 모든 댓글(106, 107, 109)이 삭제되었는지 확인합니다.

## 테스트 검증 항목

1. **댓글 생성 검증**:
   - 각 레벨의 댓글이 성공적으로 생성되었는지 확인
   - 응답에 올바른 `commentId`가 포함되어 있는지 확인
   - 응답 상태 코드가 201(Created)인지 확인

2. **댓글 조회 검증**:
   - 계층형 구조가 올바르게 반환되는지 확인
   - 각 댓글의 `children` 배열에 하위 댓글이 올바르게 포함되어 있는지 확인
   - 응답 상태 코드가 200(OK)인지 확인

3. **댓글 삭제 검증**:
   - 삭제된 댓글과 그 하위 댓글이 조회 결과에 포함되지 않는지 확인
   - 응답 상태 코드가 200(OK)인지 확인

## 예상 결과

1. 다중 레벨의 중첩 댓글이 성공적으로 생성됩니다.
2. 계층형 구조로 댓글을 조회할 수 있습니다.
3. 댓글을 삭제하면 그 하위의 모든 댓글도 함께 삭제됩니다.

## 문제 해결

테스트 중 오류가 발생하면 다음을 확인하세요:

1. 애플리케이션이 실행 중인지 확인
2. API 엔드포인트 URL이 올바른지 확인
3. 요청 본문(JSON)의 형식이 올바른지 확인
4. `parentCommentId`가 실제로 존재하는 댓글의 ID인지 확인
5. 서버 로그에서 오류 메시지 확인

이 테스트 가이드를 통해 다중 레벨 중첩 댓글 기능이 올바르게 작동하는지 확인할 수 있습니다.
