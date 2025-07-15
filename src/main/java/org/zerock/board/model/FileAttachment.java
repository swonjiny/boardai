package org.zerock.board.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileAttachment {
    private Long fileId;
    private Long boardId;
    private String originalFilename;
    private String storedFilename;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createdDate;
}
