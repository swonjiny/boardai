# 다중 레벨 중첩 댓글 기능 요약 (Multi-level Nested Comments Summary)

## 개요

이 문서는 게시판 애플리케이션에 구현된 다중 레벨 중첩 댓글 기능에 대한 요약을 제공합니다. 이 기능을 통해 사용자는 게시글에 댓글을 작성하고, 댓글에 대댓글을 작성하고, 대댓글에 또 댓글을 작성하는 등 무제한 깊이의 중첩 댓글을 생성할 수 있습니다.

## 구현된 기능

1. **다중 레벨 중첩 댓글 생성**: 사용자는 게시글에 댓글을 작성하고, 기존 댓글에 대댓글을 작성할 수 있습니다. 대댓글에 또 댓글을 작성하는 등 무제한 깊이의 중첩이 가능합니다.

2. **계층형 댓글 조회**: 게시글의 모든 댓글을 계층형 구조로 조회할 수 있습니다. 각 댓글은 자신의 하위 댓글(대댓글)을 포함하는 트리 구조로 반환됩니다.

3. **특정 댓글과 하위 댓글 조회**: 특정 댓글과 그 하위의 모든 댓글을 계층형 구조로 조회할 수 있습니다.

4. **계층적 삭제**: 댓글을 삭제하면 그 하위의 모든 댓글도 함께 삭제됩니다.

## 기술적 구현

### 데이터베이스 구조

```sql
CREATE TABLE comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    parent_comment_id BIGINT NULL,
    content LONGTEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE
);
```

핵심 필드:
- `parent_comment_id`: 부모 댓글의 ID를 저장하는 필드. 최상위 댓글은 NULL 값을 가집니다.
- 자기 참조 외래 키 제약 조건: `parent_comment_id`가 `comment_id`를 참조하도록 설정하여 자기 참조 관계를 구현합니다.
- `ON DELETE CASCADE`: 부모 댓글이 삭제되면 자동으로 하위 댓글도 삭제됩니다.

### 모델 클래스

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long commentId;
    private Long boardId;
    private Long parentCommentId;  // 부모 댓글 ID
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // 데이터베이스에 저장되지 않는 필드
    private List<Reply> replies;
    private List<Comment> children;  // 하위 댓글 목록
}
```

### 리포지토리 계층

```java
@Mapper
public interface CommentRepository {
    // 생성
    void insert(Comment comment);

    // 조회
    Optional<Comment> findById(Long commentId);
    List<Comment> findByBoardId(Long boardId);
    List<Comment> findByParentCommentId(Long parentCommentId);
    List<Comment> findTopLevelByBoardId(Long boardId);

    // 수정
    void update(Comment comment);

    // 삭제
    void deleteById(Long commentId);
    void deleteByBoardId(Long boardId);
    void deleteByParentCommentId(Long parentCommentId);
}
```

### 서비스 계층

```java
@Service
public class CommentServiceImpl implements CommentService {
    // 최상위 댓글 생성
    public Long createComment(Comment comment) {
        comment.setParentCommentId(null);
        commentRepository.insert(comment);
        return comment.getCommentId();
    }

    // 중첩 댓글 생성
    public Long createNestedComment(Comment comment) {
        // 부모 댓글 존재 여부 확인
        commentRepository.findById(comment.getParentCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        
        commentRepository.insert(comment);
        return comment.getCommentId();
    }

    // 계층형 댓글 조회
    public List<Comment> getCommentsByBoardIdWithNesting(Long boardId) {
        // 최상위 댓글 조회
        List<Comment> topLevelComments = commentRepository.findTopLevelByBoardId(boardId);
        
        // 각 댓글에 대해 하위 댓글 로드
        loadChildComments(topLevelComments);
        
        return topLevelComments;
    }

    // 하위 댓글 재귀적 로드
    private void loadChildComments(List<Comment> comments) {
        for (Comment comment : comments) {
            List<Comment> childComments = commentRepository.findByParentCommentId(comment.getCommentId());
            comment.setChildren(childComments);
            
            // 재귀적으로 하위 댓글 로드
            loadChildComments(childComments);
        }
    }

    // 댓글 삭제 (하위 댓글도 함께 삭제)
    public void deleteComment(Long commentId) {
        // 데이터베이스의 CASCADE 제약 조건으로 인해 하위 댓글이 자동으로 삭제됩니다.
        commentRepository.deleteById(commentId);
    }
}
```

### 컨트롤러 계층

```java
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    // 최상위 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody Comment comment) {
        Long commentId = commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponseDTO(commentId));
    }

    // 중첩 댓글 생성
    @PostMapping("/nested")
    public ResponseEntity<CommentResponseDTO> createNestedComment(@RequestBody Comment comment) {
        Long commentId = commentService.createNestedComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponseDTO(commentId));
    }

    // 계층형 댓글 조회
    @GetMapping("/board/{boardId}/nested")
    public ResponseEntity<List<Comment>> getNestedCommentsByBoardId(@PathVariable Long boardId) {
        List<Comment> comments = commentService.getCommentsByBoardIdWithNesting(boardId);
        return ResponseEntity.ok(comments);
    }

    // 특정 댓글과 하위 댓글 조회
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new CommentResponseDTO("댓글이 성공적으로 삭제되었습니다"));
    }
}
```

## API 엔드포인트 요약

| 메서드 | 엔드포인트 | 설명 |
|--------|------------|------|
| POST | /api/comments | 최상위 댓글 생성 |
| POST | /api/comments/nested | 중첩 댓글 생성 |
| GET | /api/comments/board/{boardId} | 게시글의 모든 댓글 조회 |
| GET | /api/comments/board/{boardId}/nested | 게시글의 모든 댓글을 계층형 구조로 조회 |
| GET | /api/comments/board/{boardId}/top-level | 게시글의 최상위 댓글만 조회 |
| GET | /api/comments/parent/{parentCommentId} | 특정 댓글의 하위 댓글 조회 |
| GET | /api/comments/{commentId} | 특정 댓글과 하위 댓글 조회 |
| PUT | /api/comments/{commentId} | 댓글 수정 |
| DELETE | /api/comments/{commentId} | 댓글과 하위 댓글 삭제 |

## 관련 문서

1. [중첩 댓글 예제](NESTED_COMMENTS_EXAMPLE.md): 다중 레벨 중첩 댓글 생성 및 조회 예제
2. [중첩 댓글 테스트 가이드](NESTED_COMMENTS_TEST.md): 중첩 댓글 기능 테스트 방법

## 결론

다중 레벨 중첩 댓글 기능은 사용자 간의 더 풍부한 상호작용을 가능하게 합니다. 이 기능을 통해 사용자는 특정 댓글에 직접 응답하고, 대화 스레드를 형성할 수 있습니다. 자기 참조 관계와 재귀적 로직을 사용하여 무제한 깊이의 중첩 댓글을 지원하도록 구현되었습니다.
