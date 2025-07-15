package org.zerock.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.board.model.FileAttachment;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FileAttachmentRepository {
    // Create
    void insert(FileAttachment file);
    void insertBatch(List<FileAttachment> files);

    // Read
    Optional<FileAttachment> findById(Long fileId);
    List<FileAttachment> findByBoardId(Long boardId);

    // Delete
    void deleteById(Long fileId);
    void deleteByBoardId(Long boardId);
}
