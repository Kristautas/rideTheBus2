package com.kristautas2.ridethebus.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kristautas2.ridethebus.core.logic.GameManager;
import com.kristautas2.ridethebus.core.model.Deck;
import com.kristautas2.ridethebus.core.model.Player;
import com.kristautas2.ridethebus.ui.screen.GameScreen;
import com.kristautas2.ridethebus.util.AssetHandler;

public class RideTheBusGame extends Game {
    private SpriteBatch batch;
    private AssetHandler assetHandler;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        assetHandler = new AssetHandler();
        assetHandler.finishLoading();
        assetHandler.initializeSkin();
        setScreen(new GameScreen(this, new GameManager(assetHandler)));

    }

    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (assetHandler != null) {
            assetHandler.dispose();
        }
    }

    //======================================

    public SpriteBatch getBatch() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        return batch;
    }

    //======================================

    public AssetHandler getAssetHandler() {
        if (assetHandler == null) {
            assetHandler = new AssetHandler();
        }
        return assetHandler;
    }
}
