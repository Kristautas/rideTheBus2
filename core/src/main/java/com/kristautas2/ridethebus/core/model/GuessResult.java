package com.kristautas2.ridethebus.core.model;

public class GuessResult {
    private final boolean success;
    private Card card;
    private final String message;

    public GuessResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    // Getters and constructor
}
