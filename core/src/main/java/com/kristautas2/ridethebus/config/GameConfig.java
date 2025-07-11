package com.kristautas2.ridethebus.config;

import com.kristautas2.ridethebus.core.logic.GameManager;

import java.util.Map;

public class GameConfig {
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    public static final float CARD_WIDTH = 100f;
    public static final float CARD_HEIGHT = 150f;
    public static final float CARD_SPACING = 10f;
    public static final float TABLE_PADDING = 10f;
    public static final float[] BACKGROUND_COLOR = {0.2f, 0.5f, 0.2f, 1f}; // RGBA for green background

    public static final Map<GameManager.GameState, Integer> PAYOUT_MULTIPLIERS = Map.of(
        GameManager.GameState.GUESS_COLOR, 2,
        GameManager.GameState.GUESS_HIGHER_LOWER, 4,
        GameManager.GameState.GUESS_INSIDE_OUTSIDE, 8,
        GameManager.GameState.GUESS_SUIT, 32
    );
}
