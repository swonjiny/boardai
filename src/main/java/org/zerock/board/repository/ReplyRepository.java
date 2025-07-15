package org.zerock.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.board.model.Reply;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReplyRepository {
    // Create
    void insert(Reply reply);

    // Read
    Optional<Reply> findById(Long replyId);
    List<Reply> findByCommentId(Long commentId);

    // Update
    void update(Reply reply);

    // Delete
    void deleteById(Long replyId);
    void deleteByCommentId(Long commentId);
}
