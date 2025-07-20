package org.zerock.board.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long commentId;
    private Long boardId;
    private Long parentCommentId;  // Added for nested comments
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // Transient fields (not stored in DB)
    private List<Reply> replies;
    private List<Comment> children;  // Added for nested comments
}
