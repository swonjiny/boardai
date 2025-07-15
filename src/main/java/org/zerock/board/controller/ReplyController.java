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
import org.zerock.board.dto.ReplyResponseDTO;
import org.zerock.board.model.Reply;
import org.zerock.board.service.ReplyService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/replies")
@RequiredArgsConstructor
@Tag(name = "답글", description = "댓글에 대한 답글 API")
public class ReplyController {

    private final ReplyService replyService;

    @Operation(summary = "새 답글 작성", description = "댓글에 새 답글을 작성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "답글이 성공적으로 생성됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReplyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<ReplyResponseDTO> createReply(
            @Parameter(description = "답글 데이터", required = true) @RequestBody Reply reply) {
        Long replyId = replyService.createReply(reply);

        ReplyResponseDTO response = ReplyResponseDTO.builder()
                .replyId(replyId)
                .message("답글이 성공적으로 생성되었습니다")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "댓글 ID로 답글 조회", description = "특정 댓글의 모든 답글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reply.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<Reply>> getRepliesByCommentId(
            @Parameter(description = "답글을 조회할 댓글의 ID", required = true, example = "1") @PathVariable Long commentId) {
        List<Reply> replies = replyService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(replies);
    }

    @Operation(summary = "ID로 답글 조회", description = "단일 답글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reply.class))),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{replyId}")
    public ResponseEntity<Reply> getReplyById(
            @Parameter(description = "조회할 답글의 ID", required = true, example = "1") @PathVariable Long replyId) {
        Reply reply = replyService.getReplyById(replyId);
        return ResponseEntity.ok(reply);
    }

    @Operation(summary = "답글 수정", description = "기존 답글을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글이 성공적으로 수정됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReplyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyResponseDTO> updateReply(
            @Parameter(description = "수정할 답글의 ID", required = true, example = "1") @PathVariable Long replyId,
            @Parameter(description = "수정된 답글 데이터", required = true) @RequestBody Reply reply) {

        reply.setReplyId(replyId);
        replyService.updateReply(reply);

        ReplyResponseDTO response = ReplyResponseDTO.builder()
                .message("답글이 성공적으로 수정되었습니다")
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "답글 삭제", description = "답글을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글이 성공적으로 삭제됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReplyResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ReplyResponseDTO> deleteReply(
            @Parameter(description = "삭제할 답글의 ID", required = true, example = "1") @PathVariable Long replyId) {
        replyService.deleteReply(replyId);

        ReplyResponseDTO response = ReplyResponseDTO.builder()
                .message("답글이 성공적으로 삭제되었습니다")
                .build();

        return ResponseEntity.ok(response);
    }
}
