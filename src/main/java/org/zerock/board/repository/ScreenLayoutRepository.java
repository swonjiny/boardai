package org.zerock.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.board.model.ScreenLayout;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for screen layout operations.
 */
@Mapper
public interface ScreenLayoutRepository {
    // Create
    void insert(ScreenLayout screenLayout);

    // Read
    Optional<ScreenLayout> findById(Long layoutId);
    List<ScreenLayout> findAll();

    // Update
    void update(ScreenLayout screenLayout);

    // Delete
    void deleteById(Long layoutId);
}
