package com.kristautas2.ridethebus.ui.render;

import com.badlogic.gdx.Game;
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
        float cardWidth = GameConfig.getCardWidth(); // Fixed width (50f)
        float cardHeight = GameConfig.getCardHeight(); // Fixed height (80f)
        return new float[]{cardWidth, cardHeight}; // Return fixed width and height
    }

    public void renderUI(Table table, GameManager.GameState state, GameManager.OpenCards openCards) {
        gameManager.saveBalance();

        table.clearChildren();
        table.setPosition(0, 0);
        table.setSize(GameConfig.getScreenWidth(), GameConfig.getScreenHeight());
        table.center();

        Label statusLabel = createStatusLabel(state);
        statusLabel.scaleBy(GameConfig.getScaleX() * 3, GameConfig.getScaleY() * 3);
        statusLabel.setPosition(
            GameConfig.getStatusPosX() - statusLabel.getWidth() / 2,
            GameConfig.getStatusPosY() - statusLabel.getHeight() / 2);
        System.out.println(GameConfig.getStatusPosX());
        System.out.println(GameConfig.getStatusPosY());
        /// HERE <----
        table.addActor(statusLabel);


        Table infoTable = new Table();
        infoTable.add(createWinningsLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
        infoTable.add(createBetLabel(gameManager.getPlayer().getCurrentBet())).minWidth(120f).pad(GameConfig.TABLE_PADDING);
        infoTable.add(createBalanceLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
        infoTable.setPosition(GameConfig.getInfoPosX(), GameConfig.getInfoPosY());
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
                break;
            case LOST:
                addLostControls(table);
        }
    }


    public Label createStatusLabel(GameManager.GameState state) {
        switch (state) {
            case START: return new Label("  Welcome to Ride the Bus! Press Start to begin.  " + gameManager.getPlayer().getHighScore(), skin);
            case BETTING: return new Label("   Place your bet!   ", skin);
            case GUESS_COLOR: return new Label("    Guess the card color: Red or Black?    " + gameManager.getPlayer().getHighScore(), skin);
            case GUESS_HIGHER_LOWER: return new Label("Will the next card be Higher or Lower?" + gameManager.getPlayer().getHighScore(), skin);
            case GUESS_INSIDE_OUTSIDE: return new Label("Will the next card be Inside or Outside the range?" + gameManager.getPlayer().getHighScore(), skin);
            case GUESS_SUIT: return new Label("Guess the suit of the next card!" + gameManager.getPlayer().getHighScore(), skin);
            case GAME_WON: return new Label("Congratulations!!! You just won $" + gameManager.getPlayer().getTotalWinnings(), skin);
            case GAME_OVER: return new Label("Game Over! Play Again?" + gameManager.getPlayer().getHighScore(), skin);
            case LOST: return new Label("You Lost Everything!" + gameManager.getPlayer().getHighScore(), skin);
            default: return new Label("Unknown state" + gameManager.getPlayer().getHighScore(), skin);
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
        startButton.setSize(GameConfig.getButtonWidth() + 200, GameConfig.getButtonHeight() + 30);
        startButton.setPosition(
            GameConfig.getButtonPosX() - (GameConfig.getButtonWidth() + 200) / 2,
            GameConfig.getButtonPosY() - GameConfig.getButtonHeight() / 2 + 80);
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.getCardPosX(i), 0);
            // EDIT: Log card position for debugging
            System.out.println("Betting card " + i + " position: (" + GameConfig.getCardPosX(i) + ", 0)");
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);



        //============================

        Table buttonTable = new Table();
        TextButton sub100 = new TextButton("-100", skin);
        TextButton sub10 = new TextButton("-10", skin);
        final TextField betField = new TextField(String.valueOf(gameManager.getPlayer().defaultBet), skin);
        TextButton add10 = new TextButton("+10", skin);
        TextButton add100 = new TextButton("+100", skin);
        sub100.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int currentBet = Integer.parseInt(betField.getText());
                    betField.setText(String.valueOf(Math.max(0, currentBet - 100)));
                } catch (NumberFormatException e) {
                    betField.setText(String.valueOf(gameManager.getPlayer().defaultBet));
                }
            }
        });
        sub10.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int currentBet = Integer.parseInt(betField.getText());
                    betField.setText(String.valueOf(Math.max(0, currentBet - 10)));
                } catch (NumberFormatException e) {
                    betField.setText(String.valueOf(gameManager.getPlayer().defaultBet));
                }
            }
        });
        add10.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(Integer.parseInt(betField.getText()) + 10 <= gameManager.getPlayer().getBalance()){
                    try {
                        int currentBet = Integer.parseInt(betField.getText());
                        betField.setText(String.valueOf(currentBet + 10));
                    } catch (NumberFormatException e) {
                        betField.setText(String.valueOf(gameManager.getPlayer().defaultBet));
                    }
                }
            }
        });
        add100.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(Integer.parseInt(betField.getText()) + 100 <= gameManager.getPlayer().getBalance()){
                    try {
                        int currentBet = Integer.parseInt(betField.getText());
                        betField.setText(String.valueOf(currentBet + 100));
                    } catch (NumberFormatException e) {
                        betField.setText(String.valueOf(gameManager.getPlayer().defaultBet));
                    }
                }
                else {
                    betField.setText(Integer.toString((int) gameManager.getPlayer().getBalance()));
                }
            }
        });
        buttonTable.add(sub100).minWidth(60f).maxWidth(60f).maxHeight(40f).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(sub10).minWidth(40f).maxWidth(50f).maxHeight(40f);
        buttonTable.add(betField).pad(GameConfig.TABLE_PADDING);
        buttonTable.add(add10).minWidth(40f).maxWidth(50f).maxHeight(40f);
        buttonTable.add(add100).minWidth(40f).maxWidth(60f).maxHeight(40f).pad(GameConfig.TABLE_PADDING);
        buttonTable.pack();
        buttonTable.setPosition(
            GameConfig.getButtonPosX() - buttonTable.getWidth() / 2,
            30 + GameConfig.getButtonPosY() + GameConfig.getButtonHeight() + GameConfig.TABLE_PADDING - GameConfig.getButtonHeight() / 2 - 30
        );
        table.addActor(buttonTable);
//

//        //============================

        TextButton betButton = new TextButton("Bet", skin);
        betButton.setSize(GameConfig.getButtonWidth() * 4.1f, GameConfig.getButtonHeight());
        betButton.setPosition(
            GameConfig.getButtonPosX() - betButton.getWidth() / 2f,
            GameConfig.getButtonPosY() - betButton.getHeight() / 2f
        );
            System.out.println("ITS GOOD: " + Integer.parseInt(betField.getText()));
            betButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(Integer.parseInt(betField.getText()) > 1){
                        try {
                            int bet = Integer.parseInt(betField.getText());
                            gameManager.placeBet(bet);
                            updateUICallback.run();
                        } catch (IllegalArgumentException e) {
                            Label statusLabel = createStatusLabel(gameManager.getCurrentState());
                            statusLabel.setText(e.getMessage());
                            statusLabel.setPosition(GameConfig.getStatusPosX(), GameConfig.getStatusPosY());
                            table.clearChildren();
                            table.addActor(statusLabel);
                            table.addActor(betField);
                            table.addActor(betButton);
                            Table infoTable = new Table();
                            infoTable.add(createWinningsLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
                            infoTable.add(createBetLabel(gameManager.getPlayer().getCurrentBet())).minWidth(20f).pad(GameConfig.TABLE_PADDING);
                            infoTable.add(createBalanceLabel(gameManager.getPlayer())).minWidth(200f).pad(GameConfig.TABLE_PADDING);
                            infoTable.setPosition(GameConfig.getInfoPosX(), GameConfig.getInfoPosY());
                            table.addActor(infoTable);
                        }
                    }
                }
            });
            //table.addActor(amountTable);
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.getCardPosX(i), 0);
            // EDIT: Log card position for debugging
            System.out.println("Color card " + i + " position: (" + GameConfig.getCardPosX(i) + ", 0)");
            cardTable.addActor(cardImage);
        }
        table.addActor(cardTable);

        //===

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
            GameConfig.getButtonPosX() - buttonTable.getWidth() / 2,
            GameConfig.getButtonPosY() - buttonTable.getHeight() / 2
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(0)));
        cardImageUp.setScaling(Scaling.fit);
        cardImageUp.setSize(cardWidth, cardHeight);
        cardImageUp.setPosition(GameConfig.getCardPosX(0), 0);
        cardTable.addActor(cardImageUp);

        for (int i = 1; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.getCardPosX(i), 0);
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
            GameConfig.getButtonPosX() - buttonTable.getWidth() / 2,
            GameConfig.getButtonPosY() - buttonTable.getHeight() / 2
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 2; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.getCardPosX(i), 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card backs
        for (int i = 2; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.getCardPosX(i), 0);
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
            GameConfig.getButtonPosX() - buttonTable.getWidth() / 2,
            GameConfig.getButtonPosY() - buttonTable.getHeight() / 2
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 3; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.getCardPosX(i), 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card back
        Image cardImage = new Image(gameManager.getCardBackTexture());
        cardImage.setScaling(Scaling.fit);
        cardImage.setSize(cardWidth, cardHeight);
        cardImage.setPosition(GameConfig.getCardPosX(3), 0);
        cardTable.addActor(cardImage);
        table.addActor(cardTable);

        //=============================

        Table buttonTable = new Table();
        buttonTable.setPosition(GameConfig.getButtonPosX(), GameConfig.getButtonPosY());

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
        gameManager.saveBalance();

        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 4; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.getCardPosX(i), 0);
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

        if(gameManager.getPlayer().defaultBet > gameManager.getPlayer().getBalance()){
            gameManager.getPlayer().defaultBet = Math.toIntExact(gameManager.getPlayer().getBalance());
        }

        gameManager.saveBalance();
        TextButton startButton = new TextButton("Play Again", skin);
        startButton.setSize(GameConfig.getButtonWidth() + 80, GameConfig.getButtonHeight());
        startButton.setPosition(
            GameConfig.getButtonPosX() - (GameConfig.getButtonWidth() + 80) / 2,
            GameConfig.getButtonPosY() - GameConfig.getButtonHeight() / 2);
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(0)));
        cardImageUp.setScaling(Scaling.fit);
        cardImageUp.setSize(cardWidth, cardHeight);
        cardImageUp.setPosition(GameConfig.getCardPosX(0), 0);
        cardTable.addActor(cardImageUp);

        for (int i = 1; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.getCardPosX(i), 0);
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 2; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.getCardPosX(i), 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card backs
        for (int i = 2; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardImage.setPosition(GameConfig.getCardPosX(i), 0);
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 3; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.getCardPosX(i), 0);
            cardTable.addActor(cardImageUp);
        }
        // EDIT: Use absolute positioning for card back
        Image cardImage = new Image(gameManager.getCardBackTexture());
        cardImage.setScaling(Scaling.fit);
        cardImage.setSize(cardWidth, cardHeight);
        cardImage.setPosition(GameConfig.getCardPosX(3), 0);
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
        cardTable.setPosition(0, GameConfig.getCardPosY());

        for (int i = 0; i < 4; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardImageUp.setPosition(GameConfig.getCardPosX(i), 0);
            cardTable.addActor(cardImageUp);
        }
        table.addActor(cardTable);
    }

    private void addLostControls(Table table) {
        TextButton tryAgain = new TextButton("Collect $100", skin);
        tryAgain.setSize(GameConfig.getButtonWidth() + 200, GameConfig.getButtonHeight() + 50);
        tryAgain.setPosition(
            GameConfig.getButtonPosX() - (GameConfig.getButtonWidth() + 200) / 2,
            GameConfig.getButtonPosY() - GameConfig.getButtonHeight() / 2 + 100);
        tryAgain.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.getPlayer().newPlayer();
                gameManager.startGame();
                updateUICallback.run();
            }
        });
        table.addActor(tryAgain);
    }
}
