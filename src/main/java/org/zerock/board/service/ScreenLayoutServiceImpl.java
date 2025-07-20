package org.zerock.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.board.model.Card;
import org.zerock.board.model.CentralMenu;
import org.zerock.board.model.ScreenLayout;
import org.zerock.board.repository.CardRepository;
import org.zerock.board.repository.CentralMenuRepository;
import org.zerock.board.repository.ScreenLayoutRepository;

import java.util.List;

/**
 * Implementation of ScreenLayoutService.
 * Manages screen layouts, cards, and central menus.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScreenLayoutServiceImpl implements ScreenLayoutService {

    private final ScreenLayoutRepository screenLayoutRepository;
    private final CardRepository cardRepository;
    private final CentralMenuRepository centralMenuRepository;

    @Override
    @Transactional
    public Long createScreenLayout(ScreenLayout screenLayout) {
        log.debug("Creating screen layout: {}", screenLayout);

        // Insert the screen layout
        screenLayoutRepository.insert(screenLayout);
        Long layoutId = screenLayout.getLayoutId();

        // Insert cards if present
        if (screenLayout.getCards() != null && !screenLayout.getCards().isEmpty()) {
            screenLayout.getCards().forEach(card -> {
                card.setLayoutId(layoutId);
                cardRepository.insert(card);
            });
        }

        // Insert central menu if present
        if (screenLayout.getCentralMenu() != null) {
            screenLayout.getCentralMenu().setLayoutId(layoutId);
            centralMenuRepository.insert(screenLayout.getCentralMenu());
        }

        return layoutId;
    }

    @Override
    @Transactional(readOnly = true)
    public ScreenLayout getScreenLayoutById(Long layoutId) {
        log.debug("Getting screen layout by ID: {}", layoutId);

        // Get the screen layout
        ScreenLayout screenLayout = screenLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Screen layout not found with ID: " + layoutId));

        // Load cards
        List<Card> cards = cardRepository.findByLayoutId(layoutId);
        screenLayout.setCards(cards);

        // Load central menu
        centralMenuRepository.findByLayoutId(layoutId).ifPresent(screenLayout::setCentralMenu);

        return screenLayout;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreenLayout> getAllScreenLayouts() {
        log.debug("Getting all screen layouts");
        List<ScreenLayout> screenLayouts = screenLayoutRepository.findAll();

        // Load cards and central menu for each layout
        screenLayouts.forEach(layout -> {
            // Load cards
            List<Card> cards = cardRepository.findByLayoutId(layout.getLayoutId());
            layout.setCards(cards);

            // Load central menu
            centralMenuRepository.findByLayoutId(layout.getLayoutId()).ifPresent(layout::setCentralMenu);
        });

        return screenLayouts;
    }

    @Override
    @Transactional
    public void updateScreenLayout(ScreenLayout screenLayout) {
        log.debug("Updating screen layout: {}", screenLayout);

        // Check if the screen layout exists
        Long layoutId = screenLayout.getLayoutId();
        screenLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Screen layout not found with ID: " + layoutId));

        // Update the screen layout
        screenLayoutRepository.update(screenLayout);

        // Update cards
        if (screenLayout.getCards() != null) {
            // Delete existing cards
            cardRepository.deleteByLayoutId(layoutId);

            // Insert new cards
            screenLayout.getCards().forEach(card -> {
                card.setLayoutId(layoutId);
                cardRepository.insert(card);
            });
        }

        // Update central menu
        if (screenLayout.getCentralMenu() != null) {
            // Delete existing central menu
            centralMenuRepository.deleteByLayoutId(layoutId);

            // Insert new central menu
            screenLayout.getCentralMenu().setLayoutId(layoutId);
            centralMenuRepository.insert(screenLayout.getCentralMenu());
        }
    }

    @Override
    @Transactional
    public void deleteScreenLayout(Long layoutId) {
        log.debug("Deleting screen layout with ID: {}", layoutId);

        // Check if the screen layout exists
        screenLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Screen layout not found with ID: " + layoutId));

        // Delete cards and central menu (this is handled by DB cascade, but we're being explicit)
        cardRepository.deleteByLayoutId(layoutId);
        centralMenuRepository.deleteByLayoutId(layoutId);

        // Delete the screen layout
        screenLayoutRepository.deleteById(layoutId);
    }
}
