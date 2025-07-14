package com.kristautas2.ridethebus.ui.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
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

    // NEW: Helper method to calculate card size based on window dimensions
    // EDIT: Modified to use fixed card sizes from GameConfig
    private float[] getCardSize() {
        float cardWidth = GameConfig.CARD_WIDTH; // Fixed width (50f)
        float cardHeight = GameConfig.CARD_HEIGHT; // Fixed height (80f)
        return new float[]{cardWidth, cardHeight}; // Return fixed width and height
    }

    public void renderUI(Table table, GameManager.GameState state, GameManager.OpenCards openCards) {
        table.clearChildren();
        table.setPosition(0, 0);
        table.setSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        table.center();

        Label statusLabel = createStatusLabel(state);
        statusLabel.setPosition(GameConfig.STATUS_POS_X, GameConfig.STATUS_POS_Y);
        table.addActor(statusLabel);


        Table infoTable = new Table();
        infoTable.add(createWinningsLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
        infoTable.add(createBetLabel(gameManager.getPlayer().getCurrentBet())).minWidth(120f).pad(GameConfig.TABLE_PADDING);
        infoTable.add(createBalanceLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
        infoTable.setPosition(GameConfig.INFO_POS_X, GameConfig.INFO_POS_Y);
        table.addActor(infoTable);

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
                addGameOverControls(table, openCards);
                break;
            case GAME_WON:
                addGameWonControls(table);
        }
    }




    public Label createStatusLabel(GameManager.GameState state) {
        switch (state) {
            case START: return new Label("  Welcome to Ride the Bus! Press Start to begin.  ", skin);
            case BETTING: return new Label("   Place your bet!   ", skin);
            case GUESS_COLOR: return new Label("    Guess the card color: Red or Black?    ", skin);
            case GUESS_HIGHER_LOWER: return new Label("Will the next card be Higher or Lower?", skin);
            case GUESS_INSIDE_OUTSIDE: return new Label("Will the next card be Inside or Outside the range?", skin);
            case GUESS_SUIT: return new Label("Guess the suit of the next card!", skin);
            case GAME_WON: return new Label("Congratulations!!! You just won $" + gameManager.getPlayer().getTotalWinnings(), skin);
            case GAME_OVER: return new Label("Game Over! Play Again?", skin);
            default: return new Label("Unknown state", skin);
        }
    }

    public Label createWinningsLabel(Player player) {
        return new Label("  Winnings: $" + player.getTotalWinnings() + "  ", skin);
    }

    private Actor createBetLabel(int currentBet) {
        return new Label(" Bet: " + gameManager.getPlayer().getCurrentBet() + " ", skin);
    }

    public Label createBalanceLabel(Player player) {
        return new Label("  Balance: $" + player.getBalance() + "  ", skin);
    }

    private void addStartButton(Table table) {
        TextButton startButton = new TextButton("Start", skin);
        startButton.setSize(GameConfig.BUTTON_WIDTH + 80, GameConfig.BUTTON_HEIGHT);
        startButton.setPosition(
            GameConfig.BUTTON_POS_X - (GameConfig.BUTTON_WIDTH + 80) / 2,
            GameConfig.BUTTON_POS_Y - GameConfig.BUTTON_HEIGHT / 2);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.startGame();
                updateUICallback.run();
            }
        });
        table.addActor(startButton);
    }

    private void addBettingControls(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.CARD_POS_X[i], 0);
            // EDIT: Log card position for debugging
            System.out.println("Betting card " + i + " position: (" + GameConfig.CARD_POS_X[i] + ", 0)");
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);

        final TextField betField = new TextField(gameManager.getPlayer().defaultBet, skin);
        betField.setSize(GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT);
        betField.setPosition(
            GameConfig.BUTTON_POS_X - GameConfig.BUTTON_WIDTH / 2,
            GameConfig.BUTTON_POS_Y + GameConfig.BUTTON_HEIGHT + GameConfig.TABLE_PADDING - GameConfig.BUTTON_HEIGHT / 2
        );
        TextButton betButton = new TextButton("Bet", skin);
        betButton.setSize(GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT);
        betButton.setPosition(
            GameConfig.BUTTON_POS_X - GameConfig.BUTTON_WIDTH / 2,
            GameConfig.BUTTON_POS_Y - GameConfig.BUTTON_HEIGHT / 2
        );
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
                    statusLabel.setPosition(GameConfig.STATUS_POS_X, GameConfig.STATUS_POS_Y);
                    table.clearChildren();
                    table.addActor(statusLabel);
                    table.addActor(betField);
                    table.addActor(betButton);
                    Table infoTable = new Table();
                    infoTable.add(createWinningsLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
                    infoTable.add(createBetLabel(gameManager.getPlayer().getCurrentBet())).minWidth(20f).pad(GameConfig.TABLE_PADDING);
                    infoTable.add(createBalanceLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
                    infoTable.setPosition(GameConfig.INFO_POS_X, GameConfig.INFO_POS_Y);
                    table.addActor(infoTable);
                }
            }
        });
        table.addActor(betField);
        table.addActor(betButton);
    }

    /*
  ██████╗ ██████╗ ██╗      ██████╗ ██████╗
 ██╔════╝██╔═══██╗██║     ██╔═══██╗██╔══██╗
 ██║     ██║   ██║██║     ██║   ██║██████╔╝
 ██║     ██║   ██║██║     ██║   ██║██╔══██║
 ╚██████╗╚██████╔╝███████╗╚██████╔╝██║  ██║
  ╚═════╝ ╚═════╝ ╚══════╝ ╚═════╝ ╚═╝  ╚═╝
*/


    private void addColorButtons(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.CARD_POS_X[i], 0);
            // EDIT: Log card position for debugging
            System.out.println("Color card " + i + " position: (" + GameConfig.CARD_POS_X[i] + ", 0)");
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);

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
        buttonTable.add(redButton).minWidth(100f).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(blackButton).minWidth(100f).pad(GameConfig.TABLE_PADDING);
        buttonTable.pack();
        buttonTable.setPosition(
            GameConfig.BUTTON_POS_X - buttonTable.getWidth() / 2,
            GameConfig.BUTTON_POS_Y - buttonTable.getHeight() / 2
        );
        table.addActor(buttonTable);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addHigherLowerButtons(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(0)));
        cardImageUp.setScaling(Scaling.fit);
        cardImageUp.setSize(cardWidth, cardHeight);
        cardImageUp.setPosition(GameConfig.CARD_POS_X[0], 0);
        cardTable.addActor(cardImageUp);

        for (int i = 1; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);

        //=============================

        Table buttonTable = new Table();
        TextButton lowerButton = new TextButton("Lower", skin);
        TextButton higherButton = new TextButton("Higher", skin);
        TextButton collectWinnings = new TextButton("Collect Winnings", skin);
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
        collectWinnings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Winnings collected");
                gameManager.collectWinnings();
                updateUICallback.run();
            }
        });
        buttonTable.add(lowerButton).minWidth(100f).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(higherButton).minWidth(100f).pad(GameConfig.TABLE_PADDING);
        buttonTable.row();
        buttonTable.add(collectWinnings).colspan(2).pad(GameConfig.TABLE_PADDING);
        // EDIT: [NEW] Finalize buttonTable layout and center it
        buttonTable.pack();
        buttonTable.setPosition(
            GameConfig.BUTTON_POS_X - buttonTable.getWidth() / 2,
            GameConfig.BUTTON_POS_Y - buttonTable.getHeight() / 2
        );
        table.addActor(buttonTable);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addInsideOutsideButtons(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 2; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card backs
        for (int i = 2; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);

        //=============================

        Table buttonTable = new Table();
        TextButton insideButton = new TextButton("Inside", skin);
        TextButton outsideButton = new TextButton("Outside", skin);
        TextButton collectWinnings = new TextButton("Collect Winnings", skin);
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
        collectWinnings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Winnings collected");
                gameManager.collectWinnings();
                updateUICallback.run();
            }
        });
        buttonTable.add(insideButton).minWidth(100f).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(outsideButton).minWidth(100f).pad(GameConfig.TABLE_PADDING);
        buttonTable.row();
        buttonTable.add(collectWinnings).colspan(2).pad(GameConfig.TABLE_PADDING);
        // EDIT: [NEW] Finalize buttonTable layout and center it
        buttonTable.pack();
        buttonTable.setPosition(
            GameConfig.BUTTON_POS_X - buttonTable.getWidth() / 2,
            GameConfig.BUTTON_POS_Y - buttonTable.getHeight() / 2
        );
        table.addActor(buttonTable);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addSuitButtons(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 3; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card back
        Image cardImage = new Image(gameManager.getCardBackTexture());
        cardImage.setScaling(Scaling.fit);
        cardImage.setSize(cardWidth, cardHeight);
        cardImage.setPosition(GameConfig.CARD_POS_X[3], 0);
        cardTable.addActor(cardImage);
        table.addActor(cardTable);

        //=============================

        Table buttonTable = new Table();
        buttonTable.setPosition(GameConfig.BUTTON_POS_X, GameConfig.BUTTON_POS_Y);

        for (String suit : Deck.SUITS) {
            TextButton suitButton = new TextButton(suit, skin);
            suitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameManager.guessSuit(suit);
                    updateUICallback.run();
                }
            });
            buttonTable.add(suitButton).minWidth(100f).pad(GameConfig.TABLE_PADDING);
        }
        buttonTable.row();
        TextButton collectWinnings = new TextButton("Collect Winnings", skin);
        collectWinnings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.collectWinnings();
                updateUICallback.run();
            }
        });
        buttonTable.add(collectWinnings).colspan(4).pad(GameConfig.TABLE_PADDING);
        table.addActor(buttonTable);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addGameWonControls(Table table){
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 4; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImageUp);
        }
        table.addActor(cardTable);
    }

    /*  ██████╗  █████╗ ███╗   ███╗███████╗     ██████╗ ██╗   ██╗███████╗██████╗
       ██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ██╔═══██╗██║   ██║██╔════╝██╔══██╗
       ██║  ███╗███████║██╔████╔██║█████╗      ██║   ██║██║   ██║█████╗  ██████╔╝
       ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝      ██║   ██║ ██║ ██║ ██╔══╝  ██╔══██╗
       ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    ╚██████╔╝╚██████╔╝███████╗██║  ██║
        ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝
    */

    private void addGameOverControls(Table table, GameManager.OpenCards openCards) {
        switch (openCards){
            case ONE: showOneCard(table);
            break;
            case TWO : showTwoCards(table);
            break;
            case THREE: showThreeCards(table);
            break;
            case FOUR : showFourCards(table);
            break;
        }

        TextButton startButton = new TextButton("Play Again", skin);
        startButton.setSize(GameConfig.BUTTON_WIDTH + 80, GameConfig.BUTTON_HEIGHT);
        startButton.setPosition(
            GameConfig.BUTTON_POS_X - (GameConfig.BUTTON_WIDTH + 80) / 2,
            GameConfig.BUTTON_POS_Y - GameConfig.BUTTON_HEIGHT / 2);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.startGame();
                updateUICallback.run();
            }
        });
        table.addActor(startButton);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    void showOneCard(Table table){
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(0)));
        cardImageUp.setScaling(Scaling.fit);
        cardImageUp.setSize(cardWidth, cardHeight);
        cardImageUp.setPosition(GameConfig.CARD_POS_X[0], 0);
        cardTable.addActor(cardImageUp);

        for (int i = 1; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    void showTwoCards(Table table){
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 2; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card backs
        for (int i = 2; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    void showThreeCards(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 3; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card back
        Image cardImage = new Image(gameManager.getCardBackTexture());
        cardImage.setScaling(Scaling.fit);
        cardImage.setSize(cardWidth, cardHeight);
        cardImage.setPosition(GameConfig.CARD_POS_X[3], 0);
        cardTable.addActor(cardImage);
        table.addActor(cardTable);
    }
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void showFourCards(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.CARD_POS_Y);

        for (int i = 0; i < 4; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.CARD_POS_X[i], 0);
            cardTable.addActor(cardImageUp);
        }
        table.addActor(cardTable);
    }

    public void dispose() {

    }
}
