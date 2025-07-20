package org.zerock.board.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    private String directory;

    @PostConstruct
    public void init() {
        try {
            // 경로 검증 및 정규화
            String normalizedDirectory = normalizeDirectory(directory);

            Path uploadPath = Paths.get(normalizedDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("파일 업로드 디렉토리 생성: {}", uploadPath.toAbsolutePath());
            } else {
                log.info("파일 업로드 디렉토리 확인: {}", uploadPath.toAbsolutePath());
            }
        } catch (InvalidPathException e) {
            log.error("잘못된 경로 형식: {}", directory, e);
            // 기본 경로로 대체
            initWithDefaultDirectory();
        } catch (Exception e) {
            log.error("파일 업로드 디렉토리 생성 실패: {}", directory, e);
            // 기본 경로로 대체
            initWithDefaultDirectory();
        }
    }

    private String normalizeDirectory(String dir) {
        if (dir == null || dir.trim().isEmpty()) {
            return getDefaultDirectory();
        }

        // 경로 정규화
        String normalized = dir.trim();

        // Windows UNC 경로 처리
        if (normalized.startsWith("\\\\")) {
            // UNC 경로에서 잘못된 문자 제거
            normalized = normalized.replaceAll("[<>:\"|?*]", "");
            // 연속된 백슬래시 정리
            normalized = normalized.replaceAll("\\\\+", "\\\\");
        }

        // 경로 끝에 슬래시 추가
        if (!normalized.endsWith(File.separator) && !normalized.endsWith("/")) {
            normalized += File.separator;
        }

        return normalized;
    }

    private String getDefaultDirectory() {
        return System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
    }

    private void initWithDefaultDirectory() {
        try {
            String defaultDir = getDefaultDirectory();
            Path uploadPath = Paths.get(defaultDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("기본 파일 업로드 디렉토리 생성: {}", uploadPath.toAbsolutePath());
            } else {
                log.info("기본 파일 업로드 디렉토리 사용: {}", uploadPath.toAbsolutePath());
            }
            // 설정 값을 기본 경로로 업데이트
            this.directory = defaultDir;
        } catch (Exception e) {
            log.error("기본 디렉토리 생성도 실패했습니다.", e);
            throw new RuntimeException("파일 업로드 디렉토리 초기화 실패", e);
        }
    }
}
