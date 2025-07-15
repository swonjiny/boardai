package org.zerock.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zerock.board.dto.CommentResponseDTO;
import org.zerock.board.model.Comment;
import org.zerock.board.model.Reply;
import org.zerock.board.service.CommentService;

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
 * 댓글 컨트롤러 테스트 클래스
 * 댓글 관련 API 엔드포인트의 기능을 테스트합니다.
 */
@SpringBootTest
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해 필요
    }

    /**
     * 새 댓글 작성 테스트
     * 새 댓글 작성 요청을 테스트합니다.
     */
    @Test
    @DisplayName("새 댓글 작성 테스트")
    public void testCreateComment() throws Exception {
        // 테스트 데이터 준비
        Comment comment = Comment.builder()
                .boardId(1L)
                .content("테스트 댓글입니다.")
                .writer("댓글 작성자")
                .build();

        // Mock 서비스 동작 설정
        when(commentService.createComment(any(Comment.class))).thenReturn(1L);

        // API 호출 및 검증
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 생성되었습니다"));
    }

    /**
     * 게시글별 댓글 조회 테스트
     * 특정 게시글에 달린 모든 댓글 조회 요청을 테스트합니다.
     */
    @Test
    @DisplayName("게시글별 댓글 조회 테스트")
    public void testGetCommentsByBoardId() throws Exception {
        // 테스트 데이터 준비
        List<Comment> comments = Arrays.asList(
                Comment.builder()
                        .commentId(1L)
                        .boardId(1L)
                        .content("첫 번째 댓글")
                        .writer("댓글 작성자1")
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .replies(new ArrayList<>())
                        .build(),
                Comment.builder()
                        .commentId(2L)
                        .boardId(1L)
                        .content("두 번째 댓글")
                        .writer("댓글 작성자2")
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .replies(new ArrayList<>())
                        .build()
        );

        // Mock 서비스 동작 설정
        when(commentService.getCommentsByBoardId(anyLong())).thenReturn(comments);

        // API 호출 및 검증
        mockMvc.perform(get("/api/comments/board/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].commentId").value(1))
                .andExpect(jsonPath("$[0].content").value("첫 번째 댓글"))
                .andExpect(jsonPath("$[1].commentId").value(2))
                .andExpect(jsonPath("$[1].content").value("두 번째 댓글"));
    }

    /**
     * 댓글 상세 조회 테스트
     * ID로 특정 댓글의 상세 정보 조회 요청을 테스트합니다.
     */
    @Test
    @DisplayName("댓글 상세 조회 테스트")
    public void testGetCommentById() throws Exception {
        // 테스트 데이터 준비
        List<Reply> replies = Arrays.asList(
                Reply.builder()
                        .replyId(1L)
                        .commentId(1L)
                        .content("첫 번째 답글")
                        .writer("답글 작성자1")
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .build()
        );

        Comment comment = Comment.builder()
                .commentId(1L)
                .boardId(1L)
                .content("테스트 댓글입니다.")
                .writer("댓글 작성자")
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .replies(replies)
                .build();

        // Mock 서비스 동작 설정
        when(commentService.getCommentById(anyLong())).thenReturn(comment);

        // API 호출 및 검증
        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.boardId").value(1))
                .andExpect(jsonPath("$.content").value("테스트 댓글입니다."))
                .andExpect(jsonPath("$.writer").value("댓글 작성자"))
                .andExpect(jsonPath("$.replies").isArray())
                .andExpect(jsonPath("$.replies.length()").value(1))
                .andExpect(jsonPath("$.replies[0].replyId").value(1))
                .andExpect(jsonPath("$.replies[0].content").value("첫 번째 답글"));
    }

    /**
     * 댓글 수정 테스트
     * 기존 댓글 수정 요청을 테스트합니다.
     */
    @Test
    @DisplayName("댓글 수정 테스트")
    public void testUpdateComment() throws Exception {
        // 테스트 데이터 준비
        Comment comment = Comment.builder()
                .commentId(1L)
                .boardId(1L)
                .content("수정된 댓글 내용입니다.")
                .writer("댓글 작성자")
                .build();

        // Mock 서비스 동작 설정
        doNothing().when(commentService).updateComment(any(Comment.class));

        // API 호출 및 검증
        mockMvc.perform(put("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 수정되었습니다"));
    }

    /**
     * 댓글 삭제 테스트
     * 댓글 삭제 요청을 테스트합니다.
     */
    @Test
    @DisplayName("댓글 삭제 테스트")
    public void testDeleteComment() throws Exception {
        // Mock 서비스 동작 설정
        doNothing().when(commentService).deleteComment(anyLong());

        // API 호출 및 검증
        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 삭제되었습니다"));
    }
}
