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
public class Board {
    private Long boardId;
    private String title;
    private String content;
    private String writer;
    private int viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // Transient fields (not stored in DB)
    private List<FileAttachment> files;
    private List<Comment> comments;
}
