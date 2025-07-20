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
import org.zerock.board.model.ScreenLayout;
import org.zerock.board.service.ScreenLayoutService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for screen layout operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/screen-layouts")
@RequiredArgsConstructor
@Tag(name = "화면 레이아웃", description = "화면 레이아웃 관리 API")
public class ScreenLayoutController {

    private final ScreenLayoutService screenLayoutService;

    @Operation(summary = "새 화면 레이아웃 생성", description = "카드와 중앙 메뉴를 포함한 새 화면 레이아웃을 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "화면 레이아웃이 성공적으로 생성됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createScreenLayout(
            @Parameter(description = "화면 레이아웃 데이터", required = true) @RequestBody ScreenLayout screenLayout) {
        log.debug("REST request to create screen layout: {}", screenLayout);

        Long layoutId = screenLayoutService.createScreenLayout(screenLayout);

        Map<String, Object> response = new HashMap<>();
        response.put("layoutId", layoutId);
        response.put("message", "화면 레이아웃이 성공적으로 생성되었습니다");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "ID로 화면 레이아웃 조회", description = "카드와 중앙 메뉴를 포함한 화면 레이아웃을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "화면 레이아웃을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScreenLayout.class))),
            @ApiResponse(responseCode = "404", description = "화면 레이아웃을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{layoutId}")
    public ResponseEntity<ScreenLayout> getScreenLayoutById(
            @Parameter(description = "조회할 화면 레이아웃의 ID", required = true, example = "1") @PathVariable Long layoutId) {
        log.debug("REST request to get screen layout by ID: {}", layoutId);

        try {
            ScreenLayout screenLayout = screenLayoutService.getScreenLayoutById(layoutId);
            return ResponseEntity.ok(screenLayout);
        } catch (RuntimeException e) {
            log.error("Error getting screen layout: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "모든 화면 레이아웃 조회", description = "카드와 중앙 메뉴를 포함한 모든 화면 레이아웃을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "화면 레이아웃을 성공적으로 조회함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<List<ScreenLayout>> getAllScreenLayouts() {
        log.debug("REST request to get all screen layouts");

        List<ScreenLayout> screenLayouts = screenLayoutService.getAllScreenLayouts();
        return ResponseEntity.ok(screenLayouts);
    }

    @Operation(summary = "화면 레이아웃 수정", description = "카드와 중앙 메뉴를 포함한 화면 레이아웃을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "화면 레이아웃이 성공적으로 수정됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "화면 레이아웃을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/{layoutId}")
    public ResponseEntity<Map<String, String>> updateScreenLayout(
            @Parameter(description = "수정할 화면 레이아웃의 ID", required = true, example = "1") @PathVariable Long layoutId,
            @Parameter(description = "수정된 화면 레이아웃 데이터", required = true) @RequestBody ScreenLayout screenLayout) {
        log.debug("REST request to update screen layout: {}", screenLayout);

        screenLayout.setLayoutId(layoutId);

        try {
            screenLayoutService.updateScreenLayout(screenLayout);

            Map<String, String> response = new HashMap<>();
            response.put("message", "화면 레이아웃이 성공적으로 수정되었습니다");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating screen layout: {}", e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "화면 레이아웃 삭제", description = "카드와 중앙 메뉴를 포함한 화면 레이아웃을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "화면 레이아웃이 성공적으로 삭제됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "화면 레이아웃을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{layoutId}")
    public ResponseEntity<Map<String, String>> deleteScreenLayout(
            @Parameter(description = "삭제할 화면 레이아웃의 ID", required = true, example = "1") @PathVariable Long layoutId) {
        log.debug("REST request to delete screen layout with ID: {}", layoutId);

        try {
            screenLayoutService.deleteScreenLayout(layoutId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "화면 레이아웃이 성공적으로 삭제되었습니다");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error deleting screen layout: {}", e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
