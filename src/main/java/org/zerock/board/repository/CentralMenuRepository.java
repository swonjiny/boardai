package org.zerock.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.board.model.CentralMenu;

import java.util.Optional;

/**
 * Repository interface for central menu operations.
 */
@Mapper
public interface CentralMenuRepository {
    // Create
    void insert(CentralMenu centralMenu);

    // Read
    Optional<CentralMenu> findById(Long menuId);
    Optional<CentralMenu> findByLayoutId(Long layoutId);

    // Update
    void update(CentralMenu centralMenu);

    // Delete
    void deleteById(Long menuId);
    void deleteByLayoutId(Long layoutId);
}
