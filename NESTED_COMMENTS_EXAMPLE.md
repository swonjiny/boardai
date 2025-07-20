# 다중 레벨 중첩 댓글 예제 (Multi-level Nested Comments Example)

이 문서는 게시글에 댓글, 댓글에 댓글, 그리고 댓글의 댓글에 댓글을 다는 방법을 설명합니다.

## 시나리오

다음과 같은 계층 구조의 댓글을 만들어 보겠습니다:

```
게시글 (Board ID: 1)
├── 댓글 1 (Level 1): "안녕하세요, 좋은 게시글입니다."
│   ├── 댓글 1-1 (Level 2): "감사합니다!"
│   │   ├── 댓글 1-1-1 (Level 3): "저도 정말 좋은 게시글이라고 생각합니다."
│   │   │   └── 댓글 1-1-1-1 (Level 4): "동의합니다!"
│   │   └── 댓글 1-1-2 (Level 3): "추가 질문이 있습니다."
│   └── 댓글 1-2 (Level 2): "더 자세한 설명 부탁드립니다."
└── 댓글 2 (Level 1): "질문이 있습니다."
    └── 댓글 2-1 (Level 2): "어떤 질문이신가요?"
        └── 댓글 2-1-1 (Level 3): "이 기능은 어떻게 구현하나요?"
```

## API 호출 예제

### 1. 최상위 댓글 생성 (Level 1)

#### 댓글 1 생성

```http
POST /api/comments
Content-Type: application/json

{
  "boardId": 1,
  "content": "안녕하세요, 좋은 게시글입니다.",
  "writer": "user1"
}
```

응답:

```json
{
  "commentId": 101,
  "message": "댓글이 성공적으로 생성되었습니다"
}
```

#### 댓글 2 생성

```http
POST /api/comments
Content-Type: application/json

{
  "boardId": 1,
  "content": "질문이 있습니다.",
  "writer": "user2"
}
```

응답:

```json
{
  "commentId": 102,
  "message": "댓글이 성공적으로 생성되었습니다"
}
```

### 2. 두 번째 레벨 댓글 생성 (Level 2)

#### 댓글 1-1 생성 (댓글 1에 대한 대댓글)

```http
POST /api/comments/nested
Content-Type: application/json

{
  "boardId": 1,
  "parentCommentId": 101,
  "content": "감사합니다!",
  "writer": "author"
}
```

응답:

```json
{
  "commentId": 103,
  "message": "대댓글이 성공적으로 생성되었습니다"
}
```

#### 댓글 1-2 생성 (댓글 1에 대한 또 다른 대댓글)

```http
POST /api/comments/nested
Content-Type: application/json

{
  "boardId": 1,
  "parentCommentId": 101,
  "content": "더 자세한 설명 부탁드립니다.",
  "writer": "user3"
}
```

응답:

```json
{
  "commentId": 104,
  "message": "대댓글이 성공적으로 생성되었습니다"
}
```

#### 댓글 2-1 생성 (댓글 2에 대한 대댓글)

```http
POST /api/comments/nested
Content-Type: application/json

{
  "boardId": 1,
  "parentCommentId": 102,
  "content": "어떤 질문이신가요?",
  "writer": "author"
}
```

응답:

```json
{
  "commentId": 105,
  "message": "대댓글이 성공적으로 생성되었습니다"
}
```

### 3. 세 번째 레벨 댓글 생성 (Level 3)

#### 댓글 1-1-1 생성 (댓글 1-1에 대한 대댓글)

```http
POST /api/comments/nested
Content-Type: application/json

{
  "boardId": 1,
  "parentCommentId": 103,
  "content": "저도 정말 좋은 게시글이라고 생각합니다.",
  "writer": "user4"
}
```

응답:

```json
{
  "commentId": 106,
  "message": "대댓글이 성공적으로 생성되었습니다"
}
```

#### 댓글 1-1-2 생성 (댓글 1-1에 대한 또 다른 대댓글)

```http
POST /api/comments/nested
Content-Type: application/json

{
  "boardId": 1,
  "parentCommentId": 103,
  "content": "추가 질문이 있습니다.",
  "writer": "user5"
}
```

응답:

```json
{
  "commentId": 107,
  "message": "대댓글이 성공적으로 생성되었습니다"
}
```

#### 댓글 2-1-1 생성 (댓글 2-1에 대한 대댓글)

```http
POST /api/comments/nested
Content-Type: application/json

{
  "boardId": 1,
  "parentCommentId": 105,
  "content": "이 기능은 어떻게 구현하나요?",
  "writer": "user2"
}
```

응답:

```json
{
  "commentId": 108,
  "message": "대댓글이 성공적으로 생성되었습니다"
}
```

### 4. 네 번째 레벨 댓글 생성 (Level 4)

#### 댓글 1-1-1-1 생성 (댓글 1-1-1에 대한 대댓글)

```http
POST /api/comments/nested
Content-Type: application/json

{
  "boardId": 1,
  "parentCommentId": 106,
  "content": "동의합니다!",
  "writer": "user6"
}
```

응답:

```json
{
  "commentId": 109,
  "message": "대댓글이 성공적으로 생성되었습니다"
}
```

## 계층형 댓글 조회

### 게시글의 모든 댓글을 계층형 구조로 조회

```http
GET /api/comments/board/1/nested
```

응답:

```json
[
  {
    "commentId": 101,
    "boardId": 1,
    "parentCommentId": null,
    "content": "안녕하세요, 좋은 게시글입니다.",
    "writer": "user1",
    "createdDate": "2025-07-20T21:00:00",
    "modifiedDate": "2025-07-20T21:00:00",
    "replies": [],
    "children": [
      {
        "commentId": 103,
        "boardId": 1,
        "parentCommentId": 101,
        "content": "감사합니다!",
        "writer": "author",
        "createdDate": "2025-07-20T21:05:00",
        "modifiedDate": "2025-07-20T21:05:00",
        "replies": [],
        "children": [
          {
            "commentId": 106,
            "boardId": 1,
            "parentCommentId": 103,
            "content": "저도 정말 좋은 게시글이라고 생각합니다.",
            "writer": "user4",
            "createdDate": "2025-07-20T21:10:00",
            "modifiedDate": "2025-07-20T21:10:00",
            "replies": [],
            "children": [
              {
                "commentId": 109,
                "boardId": 1,
                "parentCommentId": 106,
                "content": "동의합니다!",
                "writer": "user6",
                "createdDate": "2025-07-20T21:15:00",
                "modifiedDate": "2025-07-20T21:15:00",
                "replies": [],
                "children": []
              }
            ]
          },
          {
            "commentId": 107,
            "boardId": 1,
            "parentCommentId": 103,
            "content": "추가 질문이 있습니다.",
            "writer": "user5",
            "createdDate": "2025-07-20T21:12:00",
            "modifiedDate": "2025-07-20T21:12:00",
            "replies": [],
            "children": []
          }
        ]
      },
      {
        "commentId": 104,
        "boardId": 1,
        "parentCommentId": 101,
        "content": "더 자세한 설명 부탁드립니다.",
        "writer": "user3",
        "createdDate": "2025-07-20T21:07:00",
        "modifiedDate": "2025-07-20T21:07:00",
        "replies": [],
        "children": []
      }
    ]
  },
  {
    "commentId": 102,
    "boardId": 1,
    "parentCommentId": null,
    "content": "질문이 있습니다.",
    "writer": "user2",
    "createdDate": "2025-07-20T21:02:00",
    "modifiedDate": "2025-07-20T21:02:00",
    "replies": [],
    "children": [
      {
        "commentId": 105,
        "boardId": 1,
        "parentCommentId": 102,
        "content": "어떤 질문이신가요?",
        "writer": "author",
        "createdDate": "2025-07-20T21:08:00",
        "modifiedDate": "2025-07-20T21:08:00",
        "replies": [],
        "children": [
          {
            "commentId": 108,
            "boardId": 1,
            "parentCommentId": 105,
            "content": "이 기능은 어떻게 구현하나요?",
            "writer": "user2",
            "createdDate": "2025-07-20T21:13:00",
            "modifiedDate": "2025-07-20T21:13:00",
            "replies": [],
            "children": []
          }
        ]
      }
    ]
  }
]
```

### 특정 댓글과 그 하위 댓글 조회

예를 들어, 댓글 1(ID: 101)과 그 하위 댓글을 조회하려면:

```http
GET /api/comments/101
```

응답:

```json
{
  "commentId": 101,
  "boardId": 1,
  "parentCommentId": null,
  "content": "안녕하세요, 좋은 게시글입니다.",
  "writer": "user1",
  "createdDate": "2025-07-20T21:00:00",
  "modifiedDate": "2025-07-20T21:00:00",
  "replies": [],
  "children": [
    {
      "commentId": 103,
      "boardId": 1,
      "parentCommentId": 101,
      "content": "감사합니다!",
      "writer": "author",
      "createdDate": "2025-07-20T21:05:00",
      "modifiedDate": "2025-07-20T21:05:00",
      "replies": [],
      "children": [
        {
          "commentId": 106,
          "boardId": 1,
          "parentCommentId": 103,
          "content": "저도 정말 좋은 게시글이라고 생각합니다.",
          "writer": "user4",
          "createdDate": "2025-07-20T21:10:00",
          "modifiedDate": "2025-07-20T21:10:00",
          "replies": [],
          "children": [
            {
              "commentId": 109,
              "boardId": 1,
              "parentCommentId": 106,
              "content": "동의합니다!",
              "writer": "user6",
              "createdDate": "2025-07-20T21:15:00",
              "modifiedDate": "2025-07-20T21:15:00",
              "replies": [],
              "children": []
            }
          ]
        },
        {
          "commentId": 107,
          "boardId": 1,
          "parentCommentId": 103,
          "content": "추가 질문이 있습니다.",
          "writer": "user5",
          "createdDate": "2025-07-20T21:12:00",
          "modifiedDate": "2025-07-20T21:12:00",
          "replies": [],
          "children": []
        }
      ]
    },
    {
      "commentId": 104,
      "boardId": 1,
      "parentCommentId": 101,
      "content": "더 자세한 설명 부탁드립니다.",
      "writer": "user3",
      "createdDate": "2025-07-20T21:07:00",
      "modifiedDate": "2025-07-20T21:07:00",
      "replies": [],
      "children": []
    }
  ]
}
```

## 중첩 댓글 구현 방법 요약

1. **데이터베이스 구조**:
   - `comment` 테이블에 `parent_comment_id` 컬럼을 추가하여 자기 참조 관계를 설정
   - 최상위 댓글은 `parent_comment_id`가 `NULL`
   - 중첩 댓글은 부모 댓글의 ID를 `parent_comment_id`에 저장

2. **모델 클래스**:
   - `Comment` 클래스에 `parentCommentId` 필드 추가
   - `children` 리스트를 추가하여 중첩 댓글을 저장

3. **API 엔드포인트**:
   - 최상위 댓글 생성: `POST /api/comments`
   - 중첩 댓글 생성: `POST /api/comments/nested`
   - 계층형 댓글 조회: `GET /api/comments/board/{boardId}/nested`
   - 특정 댓글과 하위 댓글 조회: `GET /api/comments/{commentId}`

4. **서비스 로직**:
   - 중첩 댓글 생성 시 부모 댓글 존재 여부 확인
   - 댓글 조회 시 재귀적으로 하위 댓글 로드
   - 댓글 삭제 시 하위 댓글도 함께 삭제

이 예제를 통해 다중 레벨의 중첩 댓글을 구현하고 사용하는 방법을 확인할 수 있습니다.
