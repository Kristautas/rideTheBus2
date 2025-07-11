package com.kristautas2.ridethebus.core.logic;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.kristautas2.ridethebus.config.GameConfig;
import com.kristautas2.ridethebus.core.model.Card;
import com.kristautas2.ridethebus.core.model.Deck;
import com.kristautas2.ridethebus.core.model.Player;
import java.util.ArrayList;

public class GameManager {

    public GameState getCurrentState() {
        return currentState;
    }

    public Player getPlayer() {
        return player;
    }

    public void placeBet(int bet) {
        if (bet > player.getBalance()) {
            player.reset();
            currentState = GameState.GAME_OVER;
        } else {
            player.placeBet(bet);
            currentState = GameState.GUESS_COLOR;
        }
    }

    public void guessColor(String color) {
    Card newCard = dealNextCard();
    if (newCard == null) return;

    boolean correct = newCard.getCardColor().equals(color);
    if (correct) {
        player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
        currentState = GameState.GUESS_HIGHER_LOWER;
    } else {
        player.reset();
        currentState = GameState.GAME_OVER;
    }
}

public void guessSuit(String suit) {
    if (dealtCards.size() < 1) return;
    Card previousCard = dealtCards.get(dealtCards.size() - 1);
    Card newCard = dealNextCard();
    if (newCard == null) return;
    boolean correct = newCard.getCardSuit().equalsIgnoreCase(suit); // Use case-insensitive comparison
    if (correct) {
        player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
        currentState = GameState.GAME_OVER; // Player has won the game!
    } else {
        player.reset();
        currentState = GameState.GAME_OVER;
    }
}

public boolean guessHigherLower(boolean higher) {
    if (dealtCards.size() < 1) return false;
    Card previousCard = dealtCards.get(dealtCards.size() - 1);
    Card newCard = dealNextCard();
    if (newCard == null) return false;

    boolean correct;
    if (newCard.getCardValue() == previousCard.getCardValue()) {
        correct = false; // Equal values always lose
    } else {
        correct = higher ?
            (newCard.getCardValue() > previousCard.getCardValue()) :
            (newCard.getCardValue() < previousCard.getCardValue());
    }

    if (correct) {
        player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
        currentState = GameState.GUESS_SUIT;  // Changed from GUESS_INSIDE_OUTSIDE
    } else {
        player.reset();
        currentState = GameState.GAME_OVER;
    }
    return correct;
}

public boolean guessInsideOutside(boolean inside) {
    if (dealtCards.size() < 2) return false;
    Card card1 = dealtCards.get(dealtCards.size() - 2);
    Card card2 = dealtCards.get(dealtCards.size() - 1);
    Card newCard = dealNextCard();
    if (newCard == null) return false;

    int min = Math.min(card1.getCardValue(), card2.getCardValue());
    int max = Math.max(card1.getCardValue(), card2.getCardValue());
    int newValue = newCard.getCardValue();

    boolean isInside = (newValue > min && newValue < max);
    boolean correct = inside ? isInside : !isInside;

    if (correct) {
        player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
        currentState = GameState.GUESS_SUIT;
    } else {
        player.reset();
        currentState = GameState.GAME_OVER;
    }
    return correct;
}

    public enum GameState {START, BETTING, GUESS_COLOR, GUESS_HIGHER_LOWER, GUESS_INSIDE_OUTSIDE, GUESS_SUIT, GAME_OVER}
    private final Player player;
    private Deck deck;
    private GameState currentState;
    private final ArrayList<Card> dealtCards;

    public GameManager() {
        this.player = new Player();
        this.deck = new Deck();
        this.dealtCards = new ArrayList<>();
        this.currentState = GameState.START;
    }

    public void startGame() {
        deck = new Deck();
        dealtCards.clear();
        player.reset();
        currentState = GameState.BETTING;
    }

    // ... rest of the methods, but replace all instances of dealtCards.getItems() with just dealtCards
    // and dealtCards.getItemAt() with dealtCards.get()

    private Card dealNextCard() {
        try {
            Card card = deck.drawCard();
            dealtCards.add(card);
            return card;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public ArrayList<Card> getDealtCards() {
        return dealtCards;
    }
}
