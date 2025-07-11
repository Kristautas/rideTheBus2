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
        table.center();

        table.add(createStatusLabel(state)).center().minWidth(200f).pad(GameConfig.TABLE_PADDING).row();

        Table infoTable = new Table();
        infoTable.add(createWinningsLabel(gameManager.getPlayer())).center().minWidth(200f).pad(GameConfig.TABLE_PADDING);
        infoTable.add(createBalanceLabel(gameManager.getPlayer())).center().minWidth(200f).pad(GameConfig.TABLE_PADDING);
        table.add(infoTable);
        table.row();

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
            case GAME_OVER: return new Label("Game Over! Play Again?", skin);
            default: return new Label("Unknown state", skin);
        }
    }

    public Label createWinningsLabel(Player player) {
        return new Label("  Winnings: $" + player.getTotalWinnings() + "  ", skin);
    }

    public Label createBalanceLabel(Player player) {
        return new Label("  Balance: $" + player.getBalance() + "  ", skin);
    }

    private void addStartButton(Table table) {
        TextButton startButton = new TextButton("Start", skin);
        startButton.setSize(GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT);
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
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();

        for (int i = 0; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit); // Preserve aspect ratio
            cardImage.setSize(cardWidth, cardHeight); // Apply fixed size
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }
        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();


        final TextField betField = new TextField("10", skin);
        betField.setSize(GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT);
        TextButton betButton = new TextButton("Bet", skin);
        betButton.setSize(GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT);
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
        cardTable.center();

        for (int i = 0; i < 4; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit); // Preserve aspect ratio
            cardImage.setSize(cardWidth, cardHeight); // Apply fixed size
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }
        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();

        Table buttonTable = new Table();
        TextButton redButton = new TextButton("Red", skin);
        TextButton blackButton = new TextButton("Black", skin);
        TextButton collectWinnings = new TextButton("Collect Winnings", skin);
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
        collectWinnings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Winnings collected");
                gameManager.collectWinnings();
                updateUICallback.run();
            }
        });
        buttonTable.add(redButton).pad(GameConfig.TABLE_PADDING).minWidth(100f).uniform();
        buttonTable.add(blackButton).pad(GameConfig.TABLE_PADDING).minWidth(100f).uniform();
        buttonTable.row();
        buttonTable.add(collectWinnings).pad(GameConfig.TABLE_PADDING).colspan(2);
        table.add(buttonTable).pad(GameConfig.TABLE_PADDING).row();
        System.out.println(table.getCells());
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addHigherLowerButtons(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();

        Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(0)));
        cardImageUp.setScaling(Scaling.fit);
        cardImageUp.setSize(cardWidth, cardHeight);
        cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);

        for (int i = 0; i < 3; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }
        // EDIT: Center the card table in the main table
        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();

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
        buttonTable.add(lowerButton).pad(GameConfig.TABLE_PADDING).minWidth(100f).uniform();
        buttonTable.add(higherButton).pad(GameConfig.TABLE_PADDING).minWidth(100f).uniform();
        buttonTable.row();
        buttonTable.add(collectWinnings).pad(GameConfig.TABLE_PADDING).colspan(2);
        table.add(buttonTable).pad(GameConfig.TABLE_PADDING).row();
        System.out.println(table.getCells());
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addInsideOutsideButtons(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();

        for (int i = 0; i < 2; i++){
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }

        for (int i = 0; i < 2; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }

        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();

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
        buttonTable.add(insideButton).pad(GameConfig.TABLE_PADDING).minWidth(100f).uniform();
        buttonTable.add(outsideButton).pad(GameConfig.TABLE_PADDING).minWidth(100f).uniform();
        buttonTable.row();
        buttonTable.add(collectWinnings).pad(GameConfig.TABLE_PADDING).colspan(2);
        table.add(buttonTable).pad(GameConfig.TABLE_PADDING).row();
        System.out.println(table.getCells());
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addSuitButtons(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();

        for (int i = 0; i < 3; i++){
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }

            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);

        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();

        //=============================

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
            buttonTable.add(suitButton).pad(GameConfig.TABLE_PADDING).minWidth(100f).uniform();
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

        buttonTable.add(collectWinnings).pad(GameConfig.TABLE_PADDING).colspan(4);
        table.add(buttonTable);
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void addGameWonControls(Table table){
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();

        for (int i = 0; i < 4; i++){
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }
        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();
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

        TextButton newGameButton = new TextButton("Play Again", skin);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameManager.startGame();
                updateUICallback.run();
            }
        });
        table.add(newGameButton).minWidth(100f).uniform().row();
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

   void showOneCard(Table table){
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();
        System.out.println("Game Over showing 1 card");
        Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(0)));
        cardImageUp.setScaling(Scaling.fit);
        cardImageUp.setSize(cardWidth, cardHeight);
        cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);

        for (int i = 0; i < 3; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }
        // EDIT: Center the card table in the main table
        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    void showTwoCards(Table table){
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();
        System.out.println("Game Over showing 2 cards");
        for (int i = 0; i < 2; i++){
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }

        for (int i = 0; i < 2; i++) {
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }

        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    void showThreeCards(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();
        System.out.println("Game Over showing 3 cards");
        for (int i = 0; i < 3; i++) {
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }
            Image cardImage = new Image(gameManager.getCardBackTexture());
            cardImage.setScaling(Scaling.fit);
            cardImage.setSize(cardWidth, cardHeight);
            cardTable.add(cardImage).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);

            table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
            table.row();
    }

    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═
    //═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═★═

    private void showFourCards(Table table) {
        float[] cardSize = getCardSize();
        float cardWidth = cardSize[0];
        float cardHeight = cardSize[1];

        Table cardTable = new Table();
        cardTable.center();
        System.out.println("Game Over showing 4 cards");
        for (int i = 0; i < 4; i++){
            Image cardImageUp = new Image(gameManager.getCardTexture(gameManager.getDealtCards().get(i)));
            cardImageUp.setScaling(Scaling.fit);
            cardImageUp.setSize(cardWidth, cardHeight);
            cardTable.add(cardImageUp).size(cardWidth, cardHeight).pad(GameConfig.CARD_GAP);
        }
        table.add(cardTable).center().pad(GameConfig.TABLE_PADDING).row(); // Modified: Added center() for table alignment
        table.row();
    }

    public void dispose() {

    }
}
