package org.zerock.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.zerock.board.model.Board;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardRepository {
    // Create
    void insert(Board board);

    // Read
    Optional<Board> findById(Long boardId);
    List<Board> findAll();
    List<Board> findAllWithPaging(@Param("offset") int offset, @Param("limit") int limit);
    int count();

    // Update
    void update(Board board);
    void incrementViewCount(Long boardId);

    // Delete
    void deleteById(Long boardId);
}
