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
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            System.out.println("Attempting to render card: " + card.getCardName());
            System.out.println("Card image path: " + card.getImagePath());
            Texture texture = assetHandler.getCardTexture(card);
            if (texture == null) {
                System.out.println("Warning: Texture is null for card " + card.getCardName());
                continue;
            }

            System.out.println("Texture loaded successfully: " + texture.getWidth() + "x" + texture.getHeight());

            Image cardImage = new Image(texture);
            cardImage.setSize(GameConfig.getCardWidth(), GameConfig.getCardHeight());
            // Set absolute position using GameConfig
            cardImage.setPosition(GameConfig.getCardPosX(i), GameConfig.getCardPosY());
            System.out.println("Card positioned at: (" + GameConfig.getCardPosX(i) + ", " + GameConfig.getCardPosY() + ")");

            table.addActor(cardImage); // Add to table without layout constraints
        }
        table.row();
    }

    public void animateCard(Table table, Card card, float delay) {
    }

    public void dispose() {
    }
}
