package com.kristautas2.ridethebus.core.logic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kristautas2.ridethebus.config.GameConfig;
import com.kristautas2.ridethebus.core.model.Card;
import com.kristautas2.ridethebus.core.model.Deck;
import com.kristautas2.ridethebus.core.model.Player;
import com.kristautas2.ridethebus.util.AssetHandler;

import java.util.ArrayList;

public class GameManager {

    public enum GameState {START, BETTING, GUESS_COLOR, GUESS_HIGHER_LOWER, GUESS_INSIDE_OUTSIDE, GUESS_SUIT, GAME_OVER, GAME_WON}
    public enum OpenCards {ONE, TWO, THREE, FOUR}
    private final Player player;
    private Deck deck;
    private GameState currentState;
    private OpenCards currentCards;
    private final ArrayList<Card> dealtCards;
    private final AssetHandler assetHandler;

    public GameManager(AssetHandler assetHandler) {
        System.out.println("New Game Manager Created");
        this.player = new Player();
        this.deck = new Deck();
        this.dealtCards = new ArrayList<>();
        this.currentState = GameState.START;
        this.currentCards = OpenCards.ONE;
        this.assetHandler = assetHandler;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public OpenCards getCurrentOpenCards() {
        return currentCards;
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
    currentCards = OpenCards.ONE;
    if (correct) {
        System.out.println("The guess is correct. You guessed " + color + ", and correct was " + newCard.getCardColor());
        player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
        currentState = GameState.GUESS_HIGHER_LOWER;
    } else {
        Card endCard = dealNextCard();
        System.out.println("The guess is incorrect. You guessed " + color + ", and correct was " + newCard.getCardColor());
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
    currentCards = OpenCards.TWO;
    if (newCard.getCardValue() == previousCard.getCardValue()) {
        correct = false; // Equal values always lose
    } else {
        correct = higher ?
            (newCard.getCardValue() > previousCard.getCardValue()) :
            (newCard.getCardValue() < previousCard.getCardValue());
    }
    if (correct) {
        player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
        currentState = GameState.GUESS_INSIDE_OUTSIDE;  // Changed from GUESS_INSIDE_OUTSIDE
    } else {
        Card finalCard = dealNextCard();
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

    currentCards = OpenCards.THREE;

    if (correct) {
        player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
        currentState = GameState.GUESS_SUIT;
    } else {
        player.reset();
        currentState = GameState.GAME_OVER;
    }
    return correct;
}

    public void guessSuit(String suit) {
        if (dealtCards.size() < 1) return;
        Card previousCard = dealtCards.get(dealtCards.size() - 1);
        Card newCard = dealNextCard();
        if (newCard == null) return;
        boolean correct = newCard.getCardSuit().equalsIgnoreCase(suit); // Use case-insensitive comparison

        currentCards = OpenCards.FOUR;

        if (correct) {
            player.addWinnings(player.getCurrentBet() * GameConfig.PAYOUT_MULTIPLIERS.get(currentState));
            player.addWinningsToBalance();
            currentState = GameState.GAME_OVER; // Player has won the game!
        } else {
            player.reset();
            currentState = GameState.GAME_OVER;
        }
    }


    public Drawable getCardTexture(Card card) {
        return new TextureRegionDrawable(assetHandler.getCardTexture(card));
    }

    public void collectWinnings() {
        player.addWinningsToBalance();
        startGame();
    }

    public TextureRegionDrawable getCardBackTexture() {
        return new TextureRegionDrawable(assetHandler.getCardBack());
    }

    public void startGame() {
        deck = new Deck();
        dealtCards.clear();
        player.reset();
        currentState = GameState.BETTING;
    }

    private Card dealNextCard() {
        try {
            Card card = deck.drawCard();
            dealtCards.add(card);
            System.out.println("Dealt card: " + card.getCardName() + " (" + card.getCardValue() + ", " + card.getCardColor() + ", " + card.getCardSuit() + "");
            return card;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public ArrayList<Card> getDealtCards() {
        return dealtCards;
    }
}
