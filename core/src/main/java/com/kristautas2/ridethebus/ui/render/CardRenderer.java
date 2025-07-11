package com.kristautas2.ridethebus.ui.render;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kristautas2.ridethebus.config.GameConfig;
import com.kristautas2.ridethebus.core.model.Card;
import com.kristautas2.ridethebus.util.AssetHandler;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class CardRenderer {
    private final AssetHandler assetHandler;

    public CardRenderer(AssetHandler assetHandler) {
        this.assetHandler = assetHandler;
        if (!assetHandler.areAssetsLoaded()) {
            System.out.println("WARNING: Assets not fully loaded in CardRenderer initialization");
            assetHandler.finishLoading();
        }
    }

    public void renderCards(Table table, ArrayList<Card> cards) {
        if (table == null || cards == null) {
            System.out.println("Warning: Table or cards are null");
            return;
        }
        table.clearChildren();
        System.out.println("Rendering " + cards.size() + " cards"); // Debug log
        for (Card card : cards) {
            System.out.println("Attempting to render card: " + card.getCardName()); // Debug log
            System.out.println("Card image path: " + card.getImagePath()); // Debug log
            Texture texture = assetHandler.getCardTexture(card);
            if (texture == null) {
                System.out.println("Warning: Texture is null for card " + card.getCardName());
                continue;
            }

            System.out.println("Texture loaded successfully: " + texture.getWidth() + "x" + texture.getHeight());

            Image cardImage = new Image(texture);
            cardImage.setSize(GameConfig.CARD_WIDTH, GameConfig.CARD_HEIGHT);
            System.out.println("Card image size set to: " + GameConfig.CARD_WIDTH + "x" + GameConfig.CARD_HEIGHT);

            table.add(cardImage).pad(GameConfig.CARD_SPACING);
        }
        table.row();
    }

    public void animateCard(Table table, Card card, float delay) {
    }

    public void dispose() {
    }
}
