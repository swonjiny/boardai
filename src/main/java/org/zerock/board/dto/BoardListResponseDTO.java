package org.zerock.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.board.model.Board;

import java.util.List;

/**
 * DTO for board list response with pagination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListResponseDTO {
    private List<Board> boards;
    private int currentPage;
    private int totalItems;
    private int totalPages;
}
