package com.kristautas2.ridethebus.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kristautas2.ridethebus.config.GameConfig;
import com.kristautas2.ridethebus.core.logic.GameManager;
import com.kristautas2.ridethebus.ui.RideTheBusGame;
import com.kristautas2.ridethebus.ui.render.CardRenderer;
import com.kristautas2.ridethebus.ui.render.UIRenderer;
import com.kristautas2.ridethebus.util.AssetHandler;

public class GameScreen implements Screen {
    private final RideTheBusGame game;
    private final GameManager gameManager;
    private final CardRenderer cardRenderer;
    private final UIRenderer uiRenderer;
    private final Stage stage;
    private final Table mainTable;
    private final Image background;

    public GameScreen(RideTheBusGame game, GameManager gameManager) {
        System.out.println("Creating GameScreen");
        this.game = game;
        this.gameManager = gameManager;

        AssetHandler assetHandler = game.getAssetHandler();
        assetHandler.finishLoading();
        assetHandler.initializeSkin();
        this.cardRenderer = new CardRenderer(assetHandler);
        this.uiRenderer = new UIRenderer(assetHandler.getUISkin(), gameManager, this::updateUI);

        // Set up stage
        OrthographicCamera camera = new OrthographicCamera();
        stage = new Stage(new StretchViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // FIX: Initialize background with full window size and validate texture
        background = new Image(new TextureRegionDrawable(assetHandler.getBackgroundTexture()));
        Texture bgTexture = assetHandler.getBackgroundTexture();
        if (bgTexture != null) {
            System.out.println("Background texture loaded, size: " + bgTexture.getWidth() + "x" + bgTexture.getHeight());
            background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set initial size to window
            background.setPosition(0, 0); // Position at top-left
        } else {
            System.out.println("WARNING: Background texture is null, using fallback color");
            background.setColor(0.2f, 0.5f, 0.2f, 1); // Fallback to green
        }
        stage.addActor(background);

        // Create main table
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Initial UI update
        updateUI();
    }

    @Override
    public void render(float delta) {
        // Clear with transparent color to let background cover
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f); // Transparent clear
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // FIX: Update viewport and camera to match window size
        StretchViewport viewport = (StretchViewport) stage.getViewport();
        viewport.setWorldSize(width, height); // Set world size to window dimensions
        viewport.update(width, height, true); // Update with centering to ensure full coverage
        stage.getCamera().viewportWidth = width; // Force camera to match window
        stage.getCamera().viewportHeight = height;
        stage.getCamera().update();
        background.setSize(width, height); // Stretch background to full window size
        background.setPosition(0, 0); // Reset position to top-left
        System.out.println("Resized to: " + width + "x" + height +
            ", Background size: " + background.getWidth() + "x" + background.getHeight() +
            ", Stage world size: " + viewport.getWorldWidth() + "x" + viewport.getWorldHeight() +
            ", Camera viewport: " + stage.getCamera().viewportWidth + "x" + stage.getCamera().viewportHeight);
    }

    public void dispose() {
        stage.dispose();
        cardRenderer.dispose();
        uiRenderer.dispose();
    }


    private void updateUI() {
        mainTable.clear();
        //System.out.println("Main table size: " + mainTable.getWidth() + "x" + mainTable.getHeight());

        // Add card display
        Table cardTable = new Table();
        cardRenderer.renderCards(cardTable, gameManager.getDealtCards());
        //System.out.println("Card table children count: " + cardTable.getChildren().size);

        mainTable.add(cardTable).pad(10).row();

        // Add UI controls
        uiRenderer.renderUI(mainTable, gameManager.getCurrentState(), gameManager.getCurrentOpenCards());
    }



    //==============================

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
