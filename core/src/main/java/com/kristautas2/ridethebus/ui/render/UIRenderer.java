package com.kristautas2.ridethebus.ui.render;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kristautas2.ridethebus.config.GameConfig;
import com.kristautas2.ridethebus.core.logic.GameManager;
import com.kristautas2.ridethebus.core.model.Deck;
import com.kristautas2.ridethebus.core.model.Player;

public class UIRenderer {
    private final Skin skin;
    private final GameManager gameManager;
    private final Runnable updateUICallback;

    public UIRenderer(Skin skin, GameManager gameManager, Runnable updateUICallback) {
        this.skin = skin;
        this.gameManager = gameManager;
        this.updateUICallback = updateUICallback;
    }

    public void renderUI(Table table, GameManager.GameState state) {
        table.clearChildren();

        table.add(createStatusLabel(state)).pad(GameConfig.TABLE_PADDING).row();
        table.add(createWinningsLabel(gameManager.getPlayer())).pad(GameConfig.TABLE_PADDING).row();

        // Render state-specific UI
        switch (state) {
            case START:
                addStartButton(table);
                break;
            case BETTING:
                addBettingControls(table);
                break;
            case GUESS_COLOR:
                addColorButtons(table);
                break;
            case GUESS_HIGHER_LOWER:
                addHigherLowerButtons(table);
                break;
            case GUESS_INSIDE_OUTSIDE:
                addInsideOutsideButtons(table);
                break;
            case GUESS_SUIT:
                addSuitButtons(table);
                break;
            case GAME_OVER:
                addGameOverControls(table);
                break;
        }
    }

    public Label createStatusLabel(GameManager.GameState state) {
        switch (state) {
            case START: return new Label("Welcome to Ride the Bus! Press Start to begin.", skin);
            case BETTING: return new Label("Place your bet!", skin);
            case GUESS_COLOR: return new Label("Guess the card color: Red or Black?", skin);
            case GUESS_HIGHER_LOWER: return new Label("Will the next card be Higher or Lower?", skin);
            case GUESS_INSIDE_OUTSIDE: return new Label("Will the next card be Inside or Outside the range?", skin);
            case GUESS_SUIT: return new Label("Guess the suit of the next card!", skin);
            case GAME_OVER: return new Label("Game Over! Play Again?", skin);
            default: return new Label("Unknown state", skin);
        }
    }

    public Label createWinningsLabel(Player player) {
        return new Label("Winnings: $" + player.getTotalWinnings() + " (Balance: $" + player.getBalance() + ")", skin);
    }

    private void addStartButton(Table table) {
        TextButton startButton = new TextButton("Start", skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.startGame();
                updateUICallback.run();
            }
        });
        table.add(startButton).pad(GameConfig.TABLE_PADDING).row();
    }

    private void addBettingControls(Table table) {
        final TextField betField = new TextField("10", skin);
        TextButton betButton = new TextButton("Bet", skin);
        betButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int bet = Integer.parseInt(betField.getText());
                    gameManager.placeBet(bet);
                    updateUICallback.run();
                } catch (IllegalArgumentException e) {
                    Label statusLabel = createStatusLabel(gameManager.getCurrentState());
                    statusLabel.setText(e.getMessage());
                    table.clearChildren();
                    table.add(statusLabel).pad(GameConfig.TABLE_PADDING).row();
                    table.add(betField).pad(GameConfig.TABLE_PADDING).row();
                    table.add(betButton).pad(GameConfig.TABLE_PADDING).row();
                }
            }
        });
        table.add(betField).pad(GameConfig.TABLE_PADDING).row();
        table.add(betButton).pad(GameConfig.TABLE_PADDING).row();
    }

    private void addColorButtons(Table table) {
        Table buttonTable = new Table();
        TextButton redButton = new TextButton("Red", skin);
        TextButton blackButton = new TextButton("Black", skin);
        redButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Red button clicked"); // Debug print
                System.out.println("Current state: " + gameManager.getCurrentState()); // Deb// ug print
                gameManager.guessColor("red");
                updateUICallback.run();
            }
        });
        blackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Black button clicked"); // Debug print
                System.out.println("Current state: " + gameManager.getCurrentState()); // Debug print
                gameManager.guessColor("black");
                updateUICallback.run();
            }
        });
        buttonTable.add(redButton).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(blackButton).pad(GameConfig.TABLE_PADDING);
        table.add(buttonTable).pad(GameConfig.TABLE_PADDING).row();
    }

    private void addHigherLowerButtons(Table table) {
        Table buttonTable = new Table();
        TextButton higherButton = new TextButton("Higher", skin);
        TextButton lowerButton = new TextButton("Lower", skin);
        higherButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.guessHigherLower(true);
                updateUICallback.run();
            }
        });
        lowerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.guessHigherLower(false);
                updateUICallback.run();
            }
        });
        buttonTable.add(higherButton).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(lowerButton).pad(GameConfig.TABLE_PADDING);
        table.add(buttonTable).pad(GameConfig.TABLE_PADDING).row();
    }

    private void addInsideOutsideButtons(Table table) {
        Table buttonTable = new Table();
        TextButton insideButton = new TextButton("Inside", skin);
        TextButton outsideButton = new TextButton("Outside", skin);
        insideButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.guessInsideOutside(true);
                updateUICallback.run();
            }
        });
        outsideButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.guessInsideOutside(false);
                updateUICallback.run();
            }
        });
        buttonTable.add(insideButton).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(outsideButton).pad(GameConfig.TABLE_PADDING);
        table.add(buttonTable).pad(GameConfig.TABLE_PADDING).row();
    }

    private void addSuitButtons(Table table) {
        Table buttonTable = new Table();
        for (String suit : Deck.SUITS) {
            TextButton suitButton = new TextButton(suit, skin);
            suitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameManager.guessSuit(suit);
                    updateUICallback.run();
                }
            });
            buttonTable.add(suitButton).pad(GameConfig.TABLE_PADDING);
        }
        table.add(buttonTable).pad(GameConfig.TABLE_PADDING).row();
    }

    private void addGameOverControls(Table table) {
        TextButton newGameButton = new TextButton("Play Again", skin);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.startGame();
                updateUICallback.run();
            }
        });
        table.add(newGameButton).pad(GameConfig.TABLE_PADDING).row();
    }

    public void dispose() {

    }
}
