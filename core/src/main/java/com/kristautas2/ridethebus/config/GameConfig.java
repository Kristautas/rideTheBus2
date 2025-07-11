package com.kristautas2.ridethebus.config;

import com.kristautas2.ridethebus.core.logic.GameManager;

import java.util.Map;

public class GameConfig {
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 480;
    public static final float CARD_WIDTH = 100f;
    public static final float CARD_HEIGHT = 160f;
    public static final float CARD_SPACING = 1f;
    public static final float CARD_GAP = 10f; // Added: New constant for card gaps, adjustable
    public static final float BUTTON_WIDTH = 100f; // Added: Fixed width for UI buttons
    public static final float BUTTON_HEIGHT = 40f; // Added: Fixed height for UI buttons
    public static final float TABLE_PADDING = 10f;
    public static final float[] BACKGROUND_COLOR = {0.2f, 0.5f, 0.2f, 1f}; // RGBA for green background
    public static final float[] CARD_POS_X = {140f, 280f, 420f, 560f}; // Example x-positions
    public static final float CARD_POS_Y = 150f; // Example y-position (adjust as needed)

    public static final float STATUS_POS_X = 110f; // Center of screen
    public static final float STATUS_POS_Y = 400f; // Near top
    public static final float INFO_POS_X = 400f; // Center
    public static final float INFO_POS_Y = 350f; // Below status
    public static final float BUTTON_POS_X = 400f; // Center
    public static final float BUTTON_POS_Y = 50f; // Below cards

    public static final Map<GameManager.GameState, Integer> PAYOUT_MULTIPLIERS = Map.of(
        GameManager.GameState.GUESS_COLOR, 2,
        GameManager.GameState.GUESS_HIGHER_LOWER, 4,
        GameManager.GameState.GUESS_INSIDE_OUTSIDE, 8,
        GameManager.GameState.GUESS_SUIT, 32
    );
}
