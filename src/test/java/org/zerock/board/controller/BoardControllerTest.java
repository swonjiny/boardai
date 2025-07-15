package org.zerock.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zerock.board.dto.BoardListResponseDTO;
import org.zerock.board.dto.BoardResponseDTO;
import org.zerock.board.model.Board;
import org.zerock.board.model.Comment;
import org.zerock.board.model.FileAttachment;
import org.zerock.board.service.BoardService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 게시판 컨트롤러 테스트 클래스
 * 게시판 관련 API 엔드포인트의 기능을 테스트합니다.
 */
@SpringBootTest
public class BoardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private BoardController boardController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(boardController).build();
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해 필요
    }

    /**
     * 새 게시글 작성 테스트
     * 파일 첨부가 있는 새 게시글 작성 요청을 테스트합니다.
     */
    @Test
    @DisplayName("새 게시글 작성 테스트")
    public void testCreateBoard() throws Exception {
        // 테스트 데이터 준비
        Board board = Board.builder()
                .title("테스트 게시글")
                .content("테스트 내용입니다.")
                .writer("테스터")
                .build();

        MockMultipartFile boardFile = new MockMultipartFile(
                "board",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(board)
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.txt",
                "text/plain",
                "테스트 파일 내용".getBytes()
        );

        // Mock 서비스 동작 설정
        when(boardService.createBoard(any(Board.class), anyList())).thenReturn(1L);

        // API 호출 및 검증
        mockMvc.perform(multipart("/api/boards")
                .file(boardFile)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").value(1))
                .andExpect(jsonPath("$.message").value("게시글이 성공적으로 생성되었습니다"));
    }

    /**
     * 게시글 목록 조회 테스트
     * 페이지네이션을 포함한 게시글 목록 조회 요청을 테스트합니다.
     */
    @Test
    @DisplayName("게시글 목록 조회 테스트")
    public void testGetAllBoards() throws Exception {
        // 테스트 데이터 준비
        List<Board> boards = Arrays.asList(
                Board.builder()
                        .boardId(1L)
                        .title("첫 번째 게시글")
                        .content("첫 번째 내용")
                        .writer("작성자1")
                        .viewCount(10)
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .build(),
                Board.builder()
                        .boardId(2L)
                        .title("두 번째 게시글")
                        .content("두 번째 내용")
                        .writer("작성자2")
                        .viewCount(5)
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .build()
        );

        // Mock 서비스 동작 설정
        when(boardService.getBoardsWithPaging(anyInt(), anyInt())).thenReturn(boards);
        when(boardService.getTotalBoardCount()).thenReturn(2);

        // API 호출 및 검증
        mockMvc.perform(get("/api/boards")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boards").isArray())
                .andExpect(jsonPath("$.boards.length()").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    /**
     * 게시글 상세 조회 테스트
     * ID로 특정 게시글의 상세 정보 조회 요청을 테스트합니다.
     */
    @Test
    @DisplayName("게시글 상세 조회 테스트")
    public void testGetBoardById() throws Exception {
        // 테스트 데이터 준비
        Board board = Board.builder()
                .boardId(1L)
                .title("테스트 게시글")
                .content("테스트 내용입니다.")
                .writer("테스터")
                .viewCount(10)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .files(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        // Mock 서비스 동작 설정
        doNothing().when(boardService).incrementViewCount(anyLong());
        when(boardService.getBoardById(anyLong())).thenReturn(board);

        // API 호출 및 검증
        mockMvc.perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1))
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.content").value("테스트 내용입니다."))
                .andExpect(jsonPath("$.writer").value("테스터"))
                .andExpect(jsonPath("$.viewCount").value(10))
                .andExpect(jsonPath("$.files").isArray())
                .andExpect(jsonPath("$.comments").isArray());
    }

    /**
     * 게시글 수정 테스트
     * 기존 게시글 수정 요청을 테스트합니다.
     */
    @Test
    @DisplayName("게시글 수정 테스트")
    public void testUpdateBoard() throws Exception {
        // 테스트 데이터 준비
        Board board = Board.builder()
                .boardId(1L)
                .title("수정된 게시글")
                .content("수정된 내용입니다.")
                .writer("테스터")
                .build();

        MockMultipartFile boardFile = new MockMultipartFile(
                "board",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(board)
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "updated.txt",
                "text/plain",
                "수정된 파일 내용".getBytes()
        );

        // Mock 서비스 동작 설정
        doNothing().when(boardService).updateBoard(any(Board.class), anyList());

        // API 호출 및 검증
        mockMvc.perform(multipart("/api/boards/1")
                .file(boardFile)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 성공적으로 수정되었습니다"));
    }

    /**
     * 게시글 삭제 테스트
     * 게시글 삭제 요청을 테스트합니다.
     */
    @Test
    @DisplayName("게시글 삭제 테스트")
    public void testDeleteBoard() throws Exception {
        // Mock 서비스 동작 설정
        doNothing().when(boardService).deleteBoard(anyLong());

        // API 호출 및 검증
        mockMvc.perform(delete("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 성공적으로 삭제되었습니다"));
    }
}
