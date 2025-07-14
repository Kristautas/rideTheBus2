package com.kristautas2.ridethebus.config;

import com.badlogic.gdx.Gdx;
import com.kristautas2.ridethebus.core.logic.GameManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameConfig {
    // Reference dimensions for scaling
    private static final float REFERENCE_SCREEN_WIDTH = 800f;
    private static final float REFERENCE_SCREEN_HEIGHT = 480f;

    // Dynamic screen dimensions
    public static float getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public static float getScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    // Scaling factor for responsive design
    public static float getScaleX() {
        return getScreenWidth() / REFERENCE_SCREEN_WIDTH;
    }

    public static float getScaleY() {
        return getScreenHeight() / REFERENCE_SCREEN_HEIGHT;
    }

    // Card dimensions (scaled from reference)
    public static float getCardWidth() {
        return 100f * getScaleX();
    }

    public static float getCardHeight() {
        return 160f * getScaleY();
    }

    public static final float CARD_SPACING = 1f * getScaleX();
    public static final float CARD_GAP = 10f * getScaleX(); // Scaled gap
    public static float getButtonWidth() {
        return getScreenWidth() * (100f / REFERENCE_SCREEN_WIDTH); // Scales with screen width (100/800)
    }

    public static float getButtonHeight() {
        return getScreenHeight() * (40f / REFERENCE_SCREEN_HEIGHT); // Scales with screen height (40/480)
    }

    public static final float TABLE_PADDING = 10f * getScaleX();
    public static final float[] BACKGROUND_COLOR = {0.2f, 0.5f, 0.2f, 1f}; // RGBA for green background
    public static float getCardPosX(int index) {
        switch (index){
            case 0: return 140f * getScaleX();
            case 1: return 280f * getScaleX();
            case 2: return 420f * getScaleX();
            case 3: return 560f * getScaleX();
        }
        return 0;
    }

    public static float getCardPosY() {
        return 150f * getScaleY();
    }

    public static float getStatusPosX() {
        return 400f * getScaleX();
    }

    public static float getStatusPosY() {
        return 400f * getScaleY();
    }

    public static float getInfoPosX() {
        return 400f * getScaleX();
    }

    public static float getInfoPosY() {
        return 350f * getScaleY();
    }

    public static float getButtonPosX() {
        return 400f * getScaleX();
    }

    public static float getButtonPosY() {
        return 70f * getScaleY() - 20;
    }

    public static final Map<GameManager.GameState, Integer> PAYOUT_MULTIPLIERS;

    static {
        Map<GameManager.GameState, Integer> tempMap = new HashMap<GameManager.GameState, Integer>();
        tempMap.put(GameManager.GameState.GUESS_COLOR, 2);
        tempMap.put(GameManager.GameState.GUESS_HIGHER_LOWER, 4);
        tempMap.put(GameManager.GameState.GUESS_INSIDE_OUTSIDE, 8);
        tempMap.put(GameManager.GameState.GUESS_SUIT, 32);
        PAYOUT_MULTIPLIERS = Collections.unmodifiableMap(tempMap);
    }
}
