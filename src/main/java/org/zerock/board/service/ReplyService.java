package org.zerock.board.service;

import org.zerock.board.model.Reply;

import java.util.List;

public interface ReplyService {
    // Create
    Long createReply(Reply reply);

    // Read
    Reply getReplyById(Long replyId);
    List<Reply> getRepliesByCommentId(Long commentId);

    // Update
    void updateReply(Reply reply);

    // Delete
    void deleteReply(Long replyId);
}
