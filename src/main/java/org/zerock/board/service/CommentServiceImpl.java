package org.zerock.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.board.model.Comment;
import org.zerock.board.repository.CommentRepository;
import org.zerock.board.repository.ReplyRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    @Override
    @Transactional
    public Long createComment(Comment comment) {
        // Ensure this is a top-level comment (no parent)
        comment.setParentCommentId(null);
        commentRepository.insert(comment);
        return comment.getCommentId();
    }

    @Override
    @Transactional
    public Long createNestedComment(Comment comment) {
        // Validate that parent comment exists
        if (comment.getParentCommentId() == null) {
            throw new RuntimeException("Parent comment ID is required for nested comments");
        }

        commentRepository.findById(comment.getParentCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found with id: " + comment.getParentCommentId()));

        commentRepository.insert(comment);
        return comment.getCommentId();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        // Load replies
        comment.setReplies(replyRepository.findByCommentId(commentId));

        // Load child comments
        comment.setChildren(commentRepository.findByParentCommentId(commentId));

        // Recursively load children for each child comment
        loadChildComments(comment.getChildren());

        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBoardId(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardId(boardId);

        // Load replies for each comment
        comments.forEach(comment -> {
            comment.setReplies(replyRepository.findByCommentId(comment.getCommentId()));
        });

        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getTopLevelCommentsByBoardId(Long boardId) {
        List<Comment> comments = commentRepository.findTopLevelByBoardId(boardId);

        // Load replies for each comment
        comments.forEach(comment -> {
            comment.setReplies(replyRepository.findByCommentId(comment.getCommentId()));
        });

        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getChildCommentsByParentId(Long parentCommentId) {
        List<Comment> childComments = commentRepository.findByParentCommentId(parentCommentId);

        // Load replies for each comment
        childComments.forEach(comment -> {
            comment.setReplies(replyRepository.findByCommentId(comment.getCommentId()));
        });

        // Recursively load children for each child comment
        loadChildComments(childComments);

        return childComments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBoardIdWithNesting(Long boardId) {
        // Get top-level comments
        List<Comment> topLevelComments = commentRepository.findTopLevelByBoardId(boardId);

        // Load replies for each comment
        topLevelComments.forEach(comment -> {
            comment.setReplies(replyRepository.findByCommentId(comment.getCommentId()));
        });

        // Recursively load children for each top-level comment
        loadChildComments(topLevelComments);

        return topLevelComments;
    }

    // Helper method to recursively load child comments
    private void loadChildComments(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }

        for (Comment comment : comments) {
            // Load child comments
            List<Comment> childComments = commentRepository.findByParentCommentId(comment.getCommentId());
            comment.setChildren(childComments);

            // Load replies for each child comment
            childComments.forEach(childComment -> {
                childComment.setReplies(replyRepository.findByCommentId(childComment.getCommentId()));
            });

            // Recursively load children for each child comment
            loadChildComments(childComments);
        }
    }

    @Override
    @Transactional
    public void updateComment(Comment comment) {
        // Check if comment exists
        commentRepository.findById(comment.getCommentId())
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + comment.getCommentId()));

        // Update comment
        commentRepository.update(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        // Delete child comments first (this is handled by DB cascade, but we're being explicit)
        deleteChildComments(commentId);

        // Delete replies (this is handled by DB cascade, but we're being explicit)
        replyRepository.deleteByCommentId(commentId);

        // Delete comment
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteChildComments(Long parentCommentId) {
        // Get all child comments
        List<Comment> childComments = commentRepository.findByParentCommentId(parentCommentId);

        // Recursively delete each child comment's children
        for (Comment childComment : childComments) {
            deleteChildComments(childComment.getCommentId());

            // Delete replies for the child comment
            replyRepository.deleteByCommentId(childComment.getCommentId());

            // Delete the child comment
            commentRepository.deleteById(childComment.getCommentId());
        }
    }
}
