package org.zerock.board.service;

import org.zerock.board.model.Board;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    // Create
    Long createBoard(Board board, List<MultipartFile> files);

    // Read
    Board getBoardById(Long boardId);
    List<Board> getAllBoards();
    List<Board> getBoardsWithPaging(int page, int size);
    int getTotalBoardCount();

    // Update
    void updateBoard(Board board, List<MultipartFile> files);
    void incrementViewCount(Long boardId);

    // Delete
    void deleteBoard(Long boardId);
}
