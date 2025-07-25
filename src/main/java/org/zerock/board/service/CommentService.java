package org.zerock.board.service;

import org.zerock.board.model.Comment;

import java.util.List;

public interface CommentService {
    // Create
    Long createComment(Comment comment);
    Long createNestedComment(Comment comment);

    // Read
    Comment getCommentById(Long commentId);
    List<Comment> getCommentsByBoardId(Long boardId);
    List<Comment> getTopLevelCommentsByBoardId(Long boardId);
    List<Comment> getChildCommentsByParentId(Long parentCommentId);
    List<Comment> getCommentsByBoardIdWithNesting(Long boardId);

    // Update
    void updateComment(Comment comment);

    // Delete
    void deleteComment(Long commentId);
    void deleteChildComments(Long parentCommentId);
}
