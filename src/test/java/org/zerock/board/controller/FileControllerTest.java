package org.zerock.board.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zerock.board.model.FileAttachment;
import org.zerock.board.repository.FileAttachmentRepository;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 파일 컨트롤러 테스트 클래스
 * 파일 관련 API 엔드포인트의 기능을 테스트합니다.
 */
@SpringBootTest
public class FileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileAttachmentRepository fileAttachmentRepository;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    /**
     * 게시글별 파일 조회 테스트
     * 특정 게시글에 첨부된 모든 파일 조회 요청을 테스트합니다.
     */
    @Test
    @DisplayName("게시글별 파일 조회 테스트")
    public void testGetFilesByBoardId() throws Exception {
        // 테스트 데이터 준비
        List<FileAttachment> files = Arrays.asList(
                FileAttachment.builder()
                        .fileId(1L)
                        .boardId(1L)
                        .originalFilename("테스트파일1.txt")
                        .storedFilename("uuid-1.txt")
                        .fileSize(1024L)
                        .fileType("text/plain")
                        .createdDate(LocalDateTime.now())
                        .build(),
                FileAttachment.builder()
                        .fileId(2L)
                        .boardId(1L)
                        .originalFilename("테스트파일2.jpg")
                        .storedFilename("uuid-2.jpg")
                        .fileSize(2048L)
                        .fileType("image/jpeg")
                        .createdDate(LocalDateTime.now())
                        .build()
        );

        // Mock 리포지토리 동작 설정
        when(fileAttachmentRepository.findByBoardId(anyLong())).thenReturn(files);

        // API 호출 및 검증
        mockMvc.perform(get("/api/files/board/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fileId").value(1))
                .andExpect(jsonPath("$[0].originalFilename").value("테스트파일1.txt"))
                .andExpect(jsonPath("$[1].fileId").value(2))
                .andExpect(jsonPath("$[1].originalFilename").value("테스트파일2.jpg"));
    }

    /**
     * 파일 다운로드 테스트
     * 파일 다운로드 요청을 테스트합니다.
     * 참고: 실제 파일 시스템에 접근하는 부분은 모킹합니다.
     */
    @Test
    @DisplayName("파일 다운로드 테스트")
    public void testDownloadFile() throws Exception {
        // 테스트 데이터 준비
        FileAttachment fileAttachment = FileAttachment.builder()
                .fileId(1L)
                .boardId(1L)
                .originalFilename("테스트파일.txt")
                .storedFilename("uuid-test.txt")
                .fileSize(1024L)
                .fileType("text/plain")
                .createdDate(LocalDateTime.now())
                .build();

        // Mock 리포지토리 동작 설정
        when(fileAttachmentRepository.findById(anyLong())).thenReturn(Optional.of(fileAttachment));

        // 실제 파일 시스템 접근을 피하기 위해 테스트를 제한적으로 수행
        // 실제 파일 다운로드 로직은 통합 테스트에서 테스트하는 것이 더 적합
        mockMvc.perform(get("/api/files/1"))
                .andExpect(status().isOk());
    }

    /**
     * 파일 삭제 테스트
     * 파일 삭제 요청을 테스트합니다.
     */
    @Test
    @DisplayName("파일 삭제 테스트")
    public void testDeleteFile() throws Exception {
        // Mock 리포지토리 동작 설정
        doNothing().when(fileAttachmentRepository).deleteById(anyLong());

        // API 호출 및 검증
        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isNoContent());

        // 리포지토리 메서드 호출 확인
        verify(fileAttachmentRepository, times(1)).deleteById(1L);
    }

    /**
     * 파일 다운로드 예외 처리 테스트
     * 존재하지 않는 파일 다운로드 요청 시 예외 처리를 테스트합니다.
     */
    @Test
    @DisplayName("파일 다운로드 예외 처리 테스트")
    public void testDownloadFileNotFound() throws Exception {
        // Mock 리포지토리 동작 설정 - 파일을 찾을 수 없음
        when(fileAttachmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // API 호출 및 검증 - 예외 발생 예상
        mockMvc.perform(get("/api/files/999"))
                .andExpect(status().isInternalServerError());
    }
}
