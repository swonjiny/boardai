package org.zerock.board.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model class for screen layout configuration.
 * Represents the overall layout of the screen with cards and central menu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenLayout {
    private Long layoutId;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // Transient fields (not stored in DB)
    private List<Card> cards;
    private CentralMenu centralMenu;
}
