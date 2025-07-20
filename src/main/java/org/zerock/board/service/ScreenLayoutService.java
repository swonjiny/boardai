package org.zerock.board.service;

import org.zerock.board.model.ScreenLayout;

import java.util.List;

/**
 * Service interface for screen layout operations.
 */
public interface ScreenLayoutService {
    /**
     * Create a new screen layout with cards and central menu.
     *
     * @param screenLayout The screen layout to create
     * @return The ID of the created screen layout
     */
    Long createScreenLayout(ScreenLayout screenLayout);

    /**
     * Get a screen layout by ID, including its cards and central menu.
     *
     * @param layoutId The ID of the screen layout
     * @return The screen layout with its cards and central menu
     */
    ScreenLayout getScreenLayoutById(Long layoutId);

    /**
     * Get all screen layouts.
     *
     * @return List of all screen layouts (without cards and central menu)
     */
    List<ScreenLayout> getAllScreenLayouts();

    /**
     * Update an existing screen layout, including its cards and central menu.
     *
     * @param screenLayout The screen layout to update
     */
    void updateScreenLayout(ScreenLayout screenLayout);

    /**
     * Delete a screen layout by ID, including its cards and central menu.
     *
     * @param layoutId The ID of the screen layout to delete
     */
    void deleteScreenLayout(Long layoutId);
}
