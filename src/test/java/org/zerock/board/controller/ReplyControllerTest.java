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
import org.zerock.board.dto.ReplyResponseDTO;
import org.zerock.board.model.Reply;
import org.zerock.board.service.ReplyService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 답글 컨트롤러 테스트 클래스
 * 답글 관련 API 엔드포인트의 기능을 테스트합니다.
 */
@SpringBootTest
public class ReplyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReplyService replyService;

    @InjectMocks
    private ReplyController replyController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(replyController).build();
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해 필요
    }

    /**
     * 새 답글 작성 테스트
     * 새 답글 작성 요청을 테스트합니다.
     */
    @Test
    @DisplayName("새 답글 작성 테스트")
    public void testCreateReply() throws Exception {
        // 테스트 데이터 준비
        Reply reply = Reply.builder()
                .commentId(1L)
                .content("테스트 답글입니다.")
                .writer("답글 작성자")
                .build();

        // Mock 서비스 동작 설정
        when(replyService.createReply(any(Reply.class))).thenReturn(1L);

        // API 호출 및 검증
        mockMvc.perform(post("/api/replies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reply)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.replyId").value(1))
                .andExpect(jsonPath("$.message").value("답글이 성공적으로 생성되었습니다"));
    }

    /**
     * 댓글별 답글 조회 테스트
     * 특정 댓글에 달린 모든 답글 조회 요청을 테스트합니다.
     */
    @Test
    @DisplayName("댓글별 답글 조회 테스트")
    public void testGetRepliesByCommentId() throws Exception {
        // 테스트 데이터 준비
        List<Reply> replies = Arrays.asList(
                Reply.builder()
                        .replyId(1L)
                        .commentId(1L)
                        .content("첫 번째 답글")
                        .writer("답글 작성자1")
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .build(),
                Reply.builder()
                        .replyId(2L)
                        .commentId(1L)
                        .content("두 번째 답글")
                        .writer("답글 작성자2")
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .build()
        );

        // Mock 서비스 동작 설정
        when(replyService.getRepliesByCommentId(anyLong())).thenReturn(replies);

        // API 호출 및 검증
        mockMvc.perform(get("/api/replies/comment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].replyId").value(1))
                .andExpect(jsonPath("$[0].content").value("첫 번째 답글"))
                .andExpect(jsonPath("$[1].replyId").value(2))
                .andExpect(jsonPath("$[1].content").value("두 번째 답글"));
    }

    /**
     * 답글 상세 조회 테스트
     * ID로 특정 답글의 상세 정보 조회 요청을 테스트합니다.
     */
    @Test
    @DisplayName("답글 상세 조회 테스트")
    public void testGetReplyById() throws Exception {
        // 테스트 데이터 준비
        Reply reply = Reply.builder()
                .replyId(1L)
                .commentId(1L)
                .content("테스트 답글입니다.")
                .writer("답글 작성자")
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();

        // Mock 서비스 동작 설정
        when(replyService.getReplyById(anyLong())).thenReturn(reply);

        // API 호출 및 검증
        mockMvc.perform(get("/api/replies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replyId").value(1))
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.content").value("테스트 답글입니다."))
                .andExpect(jsonPath("$.writer").value("답글 작성자"));
    }

    /**
     * 답글 수정 테스트
     * 기존 답글 수정 요청을 테스트합니다.
     */
    @Test
    @DisplayName("답글 수정 테스트")
    public void testUpdateReply() throws Exception {
        // 테스트 데이터 준비
        Reply reply = Reply.builder()
                .replyId(1L)
                .commentId(1L)
                .content("수정된 답글 내용입니다.")
                .writer("답글 작성자")
                .build();

        // Mock 서비스 동작 설정
        doNothing().when(replyService).updateReply(any(Reply.class));

        // API 호출 및 검증
        mockMvc.perform(put("/api/replies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reply)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("답글이 성공적으로 수정되었습니다"));
    }

    /**
     * 답글 삭제 테스트
     * 답글 삭제 요청을 테스트합니다.
     */
    @Test
    @DisplayName("답글 삭제 테스트")
    public void testDeleteReply() throws Exception {
        // Mock 서비스 동작 설정
        doNothing().when(replyService).deleteReply(anyLong());

        // API 호출 및 검증
        mockMvc.perform(delete("/api/replies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("답글이 성공적으로 삭제되었습니다"));
    }
}
