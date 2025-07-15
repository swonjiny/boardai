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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.board.dto.BoardListResponseDTO;
import org.zerock.board.dto.BoardResponseDTO;
import org.zerock.board.model.Board;
import org.zerock.board.service.BoardService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "게시판", description = "질문과 답변 게시판을 위한 API")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "새 게시글 작성", description = "파일 첨부가 가능한 새로운 질문과 답변 게시글을 작성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글이 성공적으로 생성됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardResponseDTO> createBoard(
            @Parameter(description = "게시글 데이터", required = true) @RequestPart("board") Board board,
            @Parameter(description = "선택적 파일 첨부") @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Long boardId = boardService.createBoard(board, files);

        BoardResponseDTO response = BoardResponseDTO.builder()
                .boardId(boardId)
                .message("게시글이 성공적으로 생성되었습니다")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "모든 게시글 조회", description = "모든 게시글의 페이지네이션된 목록을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardListResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<BoardListResponseDTO> getAllBoards(
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 항목 수", example = "10") @RequestParam(defaultValue = "10") int size) {

        List<Board> boards = boardService.getBoardsWithPaging(page, size);
        int totalCount = boardService.getTotalBoardCount();

        BoardListResponseDTO response = BoardListResponseDTO.builder()
                .boards(boards)
                .currentPage(page)
                .totalItems(totalCount)
                .totalPages((int) Math.ceil((double) totalCount / size))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "ID로 게시글 조회", description = "파일, 댓글, 답글이 포함된 단일 게시글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Board.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getBoardById(
            @Parameter(description = "조회할 게시글의 ID", required = true, example = "1") @PathVariable Long boardId) {
        boardService.incrementViewCount(boardId);
        Board board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(board);
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정하고 선택적으로 새 파일을 첨부합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 수정됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping(value = "/{boardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardResponseDTO> updateBoard(
            @Parameter(description = "수정할 게시글의 ID", required = true, example = "1") @PathVariable Long boardId,
            @Parameter(description = "수정된 게시글 데이터", required = true) @RequestPart("board") Board board,
            @Parameter(description = "선택적 새 파일 첨부") @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        board.setBoardId(boardId);
        boardService.updateBoard(board, files);

        BoardResponseDTO response = BoardResponseDTO.builder()
                .message("게시글이 성공적으로 수정되었습니다")
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 삭제", description = "게시글과 관련된 모든 파일, 댓글, 답글을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 삭제됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> deleteBoard(
            @Parameter(description = "삭제할 게시글의 ID", required = true, example = "1") @PathVariable Long boardId) {
        boardService.deleteBoard(boardId);

        BoardResponseDTO response = BoardResponseDTO.builder()
                .message("게시글이 성공적으로 삭제되었습니다")
                .build();

        return ResponseEntity.ok(response);
    }
}
