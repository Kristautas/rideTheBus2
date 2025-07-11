package com.kristautas2.ridethebus.core.model;

public class Card {
    private final int cardValue;
    private final String cardSuit;

    public Card(int cardValue, String cardSuit) {
        this.cardValue = cardValue;
        this.cardSuit = cardSuit;
    }

    public String getCardName() {
        String name;
        switch (cardValue) {
            case 11: name = "jack"; break;
            case 12: name = "queen"; break;
            case 13: name = "king"; break;
            case 14: name = "ace"; break;
            default: name = String.valueOf(cardValue);
        }
        return name + "_of_" + cardSuit;
    }

    public String getImagePath() {
        return "cards/" + getCardName() + ".png";
    }

    public int getCardValue() {
        return cardValue;
    }

    public String getCardSuit() {
        return cardSuit;
    }

    public Object getCardColor() {
        switch (cardSuit) {
            case "hearts": return "red";
            case "diamonds": return "red";
            case "clubs": return "black";
            case "spades": return "black";
            default: return 0.5f;
        }
    }
}
