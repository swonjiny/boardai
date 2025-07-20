package org.zerock.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.board.model.Card;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for card operations.
 */
@Mapper
public interface CardRepository {
    // Create
    void insert(Card card);
    void insertBatch(List<Card> cards);

    // Read
    Optional<Card> findById(Long cardId);
    List<Card> findByLayoutId(Long layoutId);

    // Update
    void update(Card card);
    void updateBatch(List<Card> cards);

    // Delete
    void deleteById(Long cardId);
    void deleteByLayoutId(Long layoutId);
}
