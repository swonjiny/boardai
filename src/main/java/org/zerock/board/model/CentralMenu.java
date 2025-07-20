package org.zerock.board.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class for central menu component.
 * Represents the central menu at the bottom of the screen with state information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CentralMenu {
    private Long menuId;
    private Long layoutId;
    private Boolean priority;
    private Boolean expanded;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
