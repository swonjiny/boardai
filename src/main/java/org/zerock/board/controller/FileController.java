package org.zerock.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.board.config.FileUploadConfig;
import org.zerock.board.model.FileAttachment;
import org.zerock.board.repository.FileAttachmentRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "파일", description = "게시글 첨부 파일을 위한 API")
public class FileController {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileUploadConfig fileUploadConfig;

    @Operation(summary = "게시글 ID로 파일 조회", description = "특정 게시글의 모든 첨부 파일을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileAttachment.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<FileAttachment>> getFilesByBoardId(
            @Parameter(description = "파일을 조회할 게시글의 ID", required = true, example = "1") @PathVariable Long boardId) {

        log.debug("게시글 ID {}의 파일 조회 요청", boardId);

        try {
            List<FileAttachment> files = fileAttachmentRepository.findByBoardId(boardId);
            log.debug("게시글 ID {}에 대해 {} 개의 파일 조회됨", boardId, files.size());
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("게시글 ID {}의 파일 조회 중 오류 발생", boardId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "파일 다운로드", description = "첨부 파일을 다운로드합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일을 성공적으로 다운로드함",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "다운로드할 파일의 ID", required = true, example = "1") @PathVariable Long fileId) {

        log.debug("파일 ID {} 다운로드 요청", fileId);

        try {
            Optional<FileAttachment> fileOptional = fileAttachmentRepository.findById(fileId);

            if (fileOptional.isEmpty()) {
                log.warn("파일 ID {} 를 찾을 수 없음", fileId);
                return ResponseEntity.notFound().build();
            }

            FileAttachment file = fileOptional.get();
            Path filePath = Paths.get(fileUploadConfig.getDirectory(), file.getStoredFilename());

            if (!Files.exists(filePath)) {
                log.error("파일이 실제로 존재하지 않음: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            String encodedFilename = URLEncoder.encode(file.getOriginalFilename(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            log.debug("파일 다운로드 시작: {} -> {}", file.getOriginalFilename(), filePath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .header(HttpHeaders.CONTENT_TYPE, file.getFileType())
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getFileSize()))
                    .body(resource);

        } catch (Exception e) {
            log.error("파일 ID {} 다운로드 중 오류 발생", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "파일 삭제", description = "첨부 파일을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "파일이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "삭제할 파일의 ID", required = true, example = "1") @PathVariable Long fileId) {

        log.debug("파일 ID {} 삭제 요청", fileId);

        try {
            Optional<FileAttachment> fileOptional = fileAttachmentRepository.findById(fileId);

            if (fileOptional.isEmpty()) {
                log.warn("삭제할 파일 ID {} 를 찾을 수 없음", fileId);
                return ResponseEntity.notFound().build();
            }

            FileAttachment file = fileOptional.get();
            Path filePath = Paths.get(fileUploadConfig.getDirectory(), file.getStoredFilename());

            // 데이터베이스에서 파일 정보 삭제
            fileAttachmentRepository.deleteById(fileId);

            // 실제 파일 삭제
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.debug("파일 삭제 완료: {}", filePath);
            } else {
                log.warn("실제 파일이 존재하지 않음: {}", filePath);
            }

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("파일 ID {} 삭제 중 오류 발생", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
