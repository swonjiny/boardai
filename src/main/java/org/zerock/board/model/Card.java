package org.zerock.board.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class for card component.
 * Represents a card in the screen layout with position and state information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private Long cardId;
    private Long layoutId;
    private String position; // 'LEFT_1', 'LEFT_2', 'RIGHT_1', 'RIGHT_2'
    private String title;
    private Boolean horizontalCollapse;
    private Boolean verticalCollapse;
    private Boolean titleOnly;
    private Boolean expanded;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
