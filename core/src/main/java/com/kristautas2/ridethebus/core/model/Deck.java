package com.kristautas2.ridethebus.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;
    public static final String[] SUITS = {"hearts", "diamonds", "clubs", "spades"};
    public static final int MIN_VALUE = 2;
    public static final int MAX_VALUE = 14;

    public Deck() {
        cards = new ArrayList<>();
        for (String suit : SUITS) {
            for (int value = MIN_VALUE; value <= MAX_VALUE; value++) {
                cards.add(new Card(value, suit));
            }
        }
        shuffle();
    }

    public Deck(List<Card> customCards) {
        this.cards = new ArrayList<>(customCards);
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot draw from an empty deck");
        }
        System.out.println("Drawn card: " + cards.get(cards.size() - 1).getCardName() + " from deck");
        return cards.remove(cards.size() - 1);

    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
