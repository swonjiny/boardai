package org.zerock.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.board.model.Reply;
import org.zerock.board.repository.ReplyRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;

    @Override
    @Transactional
    public Long createReply(Reply reply) {
        replyRepository.insert(reply);
        return reply.getReplyId();
    }

    @Override
    @Transactional(readOnly = true)
    public Reply getReplyById(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found with id: " + replyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reply> getRepliesByCommentId(Long commentId) {
        return replyRepository.findByCommentId(commentId);
    }

    @Override
    @Transactional
    public void updateReply(Reply reply) {
        // Check if reply exists
        replyRepository.findById(reply.getReplyId())
                .orElseThrow(() -> new RuntimeException("Reply not found with id: " + reply.getReplyId()));

        // Update reply
        replyRepository.update(reply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }
}
