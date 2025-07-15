package org.zerock.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.board.model.Board;
import org.zerock.board.model.Comment;
import org.zerock.board.model.FileAttachment;
import org.zerock.board.repository.BoardRepository;
import org.zerock.board.repository.CommentRepository;
import org.zerock.board.repository.FileAttachmentRepository;
import org.zerock.board.repository.ReplyRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    private final String fileUploadDirectory = "files";

    @Override
    @Transactional
    public Long createBoard(Board board, List<MultipartFile> files) {
        // Save board
        boardRepository.insert(board);
        Long boardId = board.getBoardId();

        // Save files if any
        if (files != null && !files.isEmpty()) {
            saveFiles(boardId, files);
        }

        return boardId;
    }

    @Override
    @Transactional(readOnly = true)
    public Board getBoardById(Long boardId) {
        // Get board
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + boardId));

        // Get files
        List<FileAttachment> files = fileAttachmentRepository.findByBoardId(boardId);
        board.setFiles(files);

        // Get comments with replies
        List<Comment> comments = commentRepository.findByBoardId(boardId);
        comments.forEach(comment -> {
            comment.setReplies(replyRepository.findByCommentId(comment.getCommentId()));
        });
        board.setComments(comments);

        return board;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> getBoardsWithPaging(int page, int size) {
        int offset = (page - 1) * size;
        return boardRepository.findAllWithPaging(offset, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalBoardCount() {
        return boardRepository.count();
    }

    @Override
    @Transactional
    public void updateBoard(Board board, List<MultipartFile> files) {
        // Check if board exists
        boardRepository.findById(board.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + board.getBoardId()));

        // Update board
        boardRepository.update(board);

        // Handle files if any
        if (files != null && !files.isEmpty()) {
            saveFiles(board.getBoardId(), files);
        }
    }

    @Override
    @Transactional
    public void incrementViewCount(Long boardId) {
        boardRepository.incrementViewCount(boardId);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        // Delete board (cascading will delete files, comments, and replies)
        boardRepository.deleteById(boardId);

        // Delete physical files
        List<FileAttachment> files = fileAttachmentRepository.findByBoardId(boardId);
        for (FileAttachment file : files) {
            deletePhysicalFile(file.getStoredFilename());
        }
    }

    private void saveFiles(Long boardId, List<MultipartFile> multipartFiles) {
        List<FileAttachment> fileAttachments = new ArrayList<>();

        // Create upload directory if it doesn't exist
        File uploadDir = new File(fileUploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                try {
                    // Generate unique filename
                    String originalFilename = multipartFile.getOriginalFilename();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String storedFilename = UUID.randomUUID().toString() + extension;

                    // Save file to disk
                    Path filePath = Paths.get(fileUploadDirectory, storedFilename);
                    Files.write(filePath, multipartFile.getBytes());

                    // Create file attachment entity
                    FileAttachment fileAttachment = FileAttachment.builder()
                            .boardId(boardId)
                            .originalFilename(originalFilename)
                            .storedFilename(storedFilename)
                            .fileSize(multipartFile.getSize())
                            .fileType(multipartFile.getContentType())
                            .build();

                    fileAttachments.add(fileAttachment);
                } catch (IOException e) {
                    log.error("Failed to save file", e);
                    throw new RuntimeException("Failed to save file", e);
                }
            }
        }

        // Save file attachments to database
        if (!fileAttachments.isEmpty()) {
            fileAttachmentRepository.insertBatch(fileAttachments);
        }
    }

    private void deletePhysicalFile(String storedFilename) {
        try {
            Path filePath = Paths.get(fileUploadDirectory, storedFilename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file", e);
        }
    }
}
