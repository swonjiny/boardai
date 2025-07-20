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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.board.dto.CommentResponseDTO;
import org.zerock.board.model.Comment;
import org.zerock.board.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "게시글에 대한 댓글 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "새 댓글 작성", description = "게시글에 새 댓글을 작성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글이 성공적으로 생성됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @Parameter(description = "댓글 데이터", required = true) @RequestBody Comment comment) {
        Long commentId = commentService.createComment(comment);

        CommentResponseDTO response = CommentResponseDTO.builder()
                .commentId(commentId)
                .message("댓글이 성공적으로 생성되었습니다")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "대댓글 작성", description = "기존 댓글에 대댓글을 작성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대댓글이 성공적으로 생성됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "부모 댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/nested")
    public ResponseEntity<CommentResponseDTO> createNestedComment(
            @Parameter(description = "대댓글 데이터 (parentCommentId 필수)", required = true) @RequestBody Comment comment) {
        Long commentId = commentService.createNestedComment(comment);

        CommentResponseDTO response = CommentResponseDTO.builder()
                .commentId(commentId)
                .message("대댓글이 성공적으로 생성되었습니다")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "게시글 ID로 댓글 조회", description = "특정 게시글의 모든 댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<Comment>> getCommentsByBoardId(
            @Parameter(description = "댓글을 조회할 게시글의 ID", required = true, example = "1") @PathVariable Long boardId) {
        List<Comment> comments = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "게시글 ID로 계층형 댓글 조회", description = "특정 게시글의 모든 댓글을 계층형 구조로 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/board/{boardId}/nested")
    public ResponseEntity<List<Comment>> getNestedCommentsByBoardId(
            @Parameter(description = "댓글을 조회할 게시글의 ID", required = true, example = "1") @PathVariable Long boardId) {
        List<Comment> comments = commentService.getCommentsByBoardIdWithNesting(boardId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "게시글 ID로 최상위 댓글만 조회", description = "특정 게시글의 최상위 댓글만 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/board/{boardId}/top-level")
    public ResponseEntity<List<Comment>> getTopLevelCommentsByBoardId(
            @Parameter(description = "댓글을 조회할 게시글의 ID", required = true, example = "1") @PathVariable Long boardId) {
        List<Comment> comments = commentService.getTopLevelCommentsByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "부모 댓글 ID로 대댓글 조회", description = "특정 댓글의 모든 대댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "부모 댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/parent/{parentCommentId}")
    public ResponseEntity<List<Comment>> getChildCommentsByParentId(
            @Parameter(description = "대댓글을 조회할 부모 댓글의 ID", required = true, example = "1") @PathVariable Long parentCommentId) {
        List<Comment> comments = commentService.getChildCommentsByParentId(parentCommentId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "ID로 댓글 조회", description = "답글과 대댓글이 포함된 단일 댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(
            @Parameter(description = "조회할 댓글의 ID", required = true, example = "1") @PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 수정됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @Parameter(description = "수정할 댓글의 ID", required = true, example = "1") @PathVariable Long commentId,
            @Parameter(description = "수정된 댓글 데이터", required = true) @RequestBody Comment comment) {

        comment.setCommentId(commentId);
        commentService.updateComment(comment);

        CommentResponseDTO response = CommentResponseDTO.builder()
                .message("댓글이 성공적으로 수정되었습니다")
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 삭제", description = "댓글과 모든 대댓글 및 답글을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 삭제됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> deleteComment(
            @Parameter(description = "삭제할 댓글의 ID", required = true, example = "1") @PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        CommentResponseDTO response = CommentResponseDTO.builder()
                .message("댓글이 성공적으로 삭제되었습니다")
                .build();

        return ResponseEntity.ok(response);
    }
}
