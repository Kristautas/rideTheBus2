package com.kristautas2.ridethebus.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
        this.game = game;
        this.gameManager = gameManager;

        // Initialize renderers
        AssetHandler assetHandler = game.getAssetHandler();
        this.cardRenderer = new CardRenderer(assetHandler);
        this.uiRenderer = new UIRenderer(assetHandler.getUISkin(), gameManager, this::updateUI);

        // Set up stage
        OrthographicCamera camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // Initialize background
        background = new Image(new TextureRegionDrawable(assetHandler.getBackgroundTexture()));

        // Create main table
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Initial UI update
        updateUI();
    }

    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.5f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        if (background != null) {
            game.getBatch().begin();
            background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            background.draw(game.getBatch(), 1f);
            game.getBatch().end();
        }
        // Update and render stage
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        cardRenderer.dispose();
        uiRenderer.dispose();
    }


    private void updateUI() {
        mainTable.clear();

        // Add card display
        Table cardTable = new Table();
        cardRenderer.renderCards(cardTable, gameManager.getDealtCards());
        mainTable.add(cardTable).pad(10).row();

        // Add UI controls
        uiRenderer.renderUI(mainTable, gameManager.getCurrentState());
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
