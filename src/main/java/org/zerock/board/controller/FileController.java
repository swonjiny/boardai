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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.board.model.FileAttachment;
import org.zerock.board.repository.FileAttachmentRepository;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "파일", description = "게시글 첨부 파일을 위한 API")
public class FileController {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final String fileUploadDirectory = "files";

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
        List<FileAttachment> files = fileAttachmentRepository.findByBoardId(boardId);
        return ResponseEntity.ok(files);
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
        try {
            // 데이터베이스에서 파일 찾기
            FileAttachment fileAttachment = fileAttachmentRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("ID가 " + fileId + "인 파일을 찾을 수 없습니다"));

            // 파일 경로 생성
            Path filePath = Paths.get(fileUploadDirectory).resolve(fileAttachment.getStoredFilename()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // 파일이 존재하고 읽을 수 있는지 확인
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일을 읽을 수 없습니다: " + fileAttachment.getOriginalFilename());
            }

            // 다양한 브라우저를 위한 파일명 인코딩
            String encodedFilename = URLEncoder.encode(fileAttachment.getOriginalFilename(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            // 콘텐츠 타입 및 첨부 헤더 설정
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileAttachment.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("파일 다운로드 오류", e);
            throw new RuntimeException("파일 다운로드 오류", e);
        } catch (Exception e) {
            log.error("파일 다운로드 오류", e);
            throw new RuntimeException("파일 다운로드 오류", e);
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
        fileAttachmentRepository.deleteById(fileId);
        return ResponseEntity.noContent().build();
    }
}
