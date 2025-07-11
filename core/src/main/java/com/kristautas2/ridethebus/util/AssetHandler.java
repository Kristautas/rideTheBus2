package com.kristautas2.ridethebus.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.kristautas2.ridethebus.core.model.Card;
import com.kristautas2.ridethebus.core.model.Deck;

public class AssetHandler {
    private final AssetManager assetManager;
    private final Skin skin;

    public AssetHandler() {
        assetManager = new AssetManager();
        skin = createBasicSkin();
        loadAssets();
        // Move finishLoading() to RideTheBusGame to avoid blocking
    }

    private Skin createBasicSkin() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.LIGHT_GRAY;
        skin.add("default", textButtonStyle);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = null;
        skin.add("default", textFieldStyle);

        return skin;
    }

    public void loadAssets() {
        for (String suit : Deck.SUITS) {
            for (int value = Deck.MIN_VALUE; value <= Deck.MAX_VALUE; value++) {
                Card card = new Card(value, suit);
                String assetPath = card.getImagePath();
                assetManager.load(assetPath, Texture.class);
            }
        }
        assetManager.load("cards/cardBack.png", Texture.class); // Add fallback texture
        assetManager.load("backgroundMain.png", Texture.class);
    }

    public Texture getCardTexture(Card card) {
        String assetPath = card.getImagePath();
        try {
            return assetManager.get(assetPath, Texture.class);
        } catch (Exception e) {
            System.out.println("Failed to load card texture: " + assetPath);
            return assetManager.get("cards/cardBack.png", Texture.class);
        }
    }

    public Skin getUISkin() {
        return skin;
    }

    public Texture getBackgroundTexture() {
        try {
            return assetManager.get("backgroundMain.png", Texture.class);
        } catch (Exception e) {
            System.out.println("Failed to load background texture");
            return null;
        }
    }

    public void finishLoading() {
        assetManager.finishLoading();
    }

    public void dispose() {
        if (skin != null) {
            skin.dispose();
        }
        assetManager.dispose();
    }
}
