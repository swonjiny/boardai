package org.zerock.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reply creation, update, and deletion responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponseDTO {
    private Long replyId;
    private String message;
}
