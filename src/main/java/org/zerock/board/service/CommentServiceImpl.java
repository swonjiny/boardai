package org.zerock.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.board.model.Comment;
import org.zerock.board.repository.CommentRepository;
import org.zerock.board.repository.ReplyRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    @Override
    @Transactional
    public Long createComment(Comment comment) {
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
        // Delete replies first (this is handled by DB cascade, but we're being explicit)
        replyRepository.deleteByCommentId(commentId);

        // Delete comment
        commentRepository.deleteById(commentId);
    }
}
