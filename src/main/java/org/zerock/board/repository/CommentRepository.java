package org.zerock.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.board.model.Comment;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentRepository {
    // Create
    void insert(Comment comment);

    // Read
    Optional<Comment> findById(Long commentId);
    List<Comment> findByBoardId(Long boardId);

    // Update
    void update(Comment comment);

    // Delete
    void deleteById(Long commentId);
    void deleteByBoardId(Long boardId);
}
