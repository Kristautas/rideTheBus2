package com.kristautas2.ridethebus.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kristautas2.ridethebus.core.model.Card;
import com.kristautas2.ridethebus.core.model.Deck;

public class AssetHandler {
    private final AssetManager assetManager;
    private Skin skin;

    public AssetHandler() {
        assetManager = new AssetManager();
        loadAssets();
    }


    // FIX: Defer skin creation until assets are loaded
    public void initializeSkin() {
        if (skin == null) {
            assetManager.finishLoading(); // Ensure all assets are loaded
            skin = createBasicSkin();
            System.out.println("Skin initialized with loaded assets");
        }
    }

    private Skin createBasicSkin() {
        System.out.println("Creating skin. AssetManager progress: " + assetManager.getProgress());
        assetManager.getAssetNames().forEach(name -> System.out.println("Loaded: " + name));
        Skin skin = new Skin();

        // Existing: Font size is fixed at 24, suitable for consistent UI
        BitmapFont font;
        if (Gdx.files.internal("fonts/Roboto-Regular.ttf").exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24; // Fixed font size for labels and buttons
            parameter.color = Color.WHITE;
            parameter.borderColor = Color.BLACK;
            parameter.borderWidth = 1;
            font = generator.generateFont(parameter);
            generator.dispose();
            System.out.println("Font generated successfully from Roboto-Regular.ttf");
        } else {
            System.out.println("ERROR: Roboto-Regular.ttf not found, using default BitmapFont");
            font = new BitmapFont(); // Fallback
        }
        skin.add("default-font", font);

        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.LIGHT_GRAY;

        try {
            Texture buttonTexture = assetManager.get("ui/button.9.png", Texture.class);
            Texture buttonDownTexture = assetManager.get("ui/button_down.9.png", Texture.class);
            Texture buttonOverTexture = assetManager.get("ui/button_over.9.png", Texture.class);

            // Define NinePatch with stretchable regions (adjust values based on your texture)
            NinePatch buttonPatch = new NinePatch(buttonTexture, 12, 12, 12, 12); // left, right, top, bottom
            buttonPatch.setPadding(10, 10, 5, 5);
            NinePatch buttonDownPatch = new NinePatch(buttonDownTexture, 12, 12, 12, 12);
            buttonDownPatch.setPadding(10, 10, 5, 5);
            NinePatch buttonOverPatch = new NinePatch(buttonOverTexture, 12, 12, 12, 12);
            buttonOverPatch.setPadding(10, 10, 5, 5);

            // Create NinePatchDrawable for each state
            textButtonStyle.up = new NinePatchDrawable(buttonPatch);
            textButtonStyle.down = new NinePatchDrawable(buttonDownPatch);
            textButtonStyle.over = new NinePatchDrawable(buttonOverPatch);
            System.out.println("Button NinePatch textures loaded successfully");
        } catch (Exception e) {
            System.out.println("Failed to load button textures: " + e.getMessage());
            textButtonStyle.up = createFallbackNinePatch(new Color(0.29f, 0.56f, 0.89f, 1), "ui/button.9.png");
            textButtonStyle.down = createFallbackNinePatch(new Color(0.21f, 0.48f, 0.74f, 1), "ui/button_down.9.png");
            textButtonStyle.over = createFallbackNinePatch(new Color(0.42f, 0.69f, 1.0f, 1), "ui/button_over.9.png");
        }
        skin.add("default", textButtonStyle);

// LabelStyle setup
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        try {
            if (!assetManager.isLoaded("ui/label_background.9.png")) {
                throw new GdxRuntimeException("Label background nine-patch not loaded");
            }
            Texture labelTexture = assetManager.get("ui/label_background.9.png", Texture.class);
            NinePatch labelPatch = new NinePatch(labelTexture, 12, 12, 12, 12); // Adjust patch values as needed
            labelPatch.setPadding(10, 10, 5, 5);
            labelStyle.background = new NinePatchDrawable(labelPatch).tint(new Color(1f, 1f, 1f, 0.1f));
            System.out.println("Label background nine-patch loaded successfully");
        } catch (Exception e) {
            System.out.println("Failed to load label background texture: " + e.getMessage());
            labelStyle.background = createFallbackTexture(new Color(0.1f, 0.1f, 0.1f, 1), "ui/label_background.9.png");
        }
        skin.add("default", labelStyle);
        System.out.println("LabelStyle 'default' added to skin");

        // MODERN_UI: Create text field style with background and cursor
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        try {
            if (!assetManager.isLoaded("ui/textfield_background.9.png")) {
                throw new GdxRuntimeException("Text field background nine-patch not loaded");
            }
            Texture backgroundTexture = assetManager.get("ui/textfield_background.9.png", Texture.class);
            NinePatch backgroundPatch = new NinePatch(backgroundTexture, 12, 12, 12, 12);
            backgroundPatch.setPadding(10, 10, 5, 5);
            textFieldStyle.background = new NinePatchDrawable(backgroundPatch).tint(new Color(1f, 1f, 1f, 0.4f));

            Texture cursorTexture = assetManager.get("ui/textfield_cursor.png", Texture.class);
            TextureRegionDrawable cursorDrawable = new TextureRegionDrawable(new TextureRegion(cursorTexture));
            cursorDrawable.setMinWidth(3f);
            cursorDrawable.setMinHeight(36f);
            textFieldStyle.cursor = cursorDrawable;
            System.out.println("Text field textures loaded successfully");

            /*
            Texture cursorTexture = assetManager.get("ui/textfield_cursor.9.png", Texture.class);
            TextureRegionDrawable cursorDrawable = new TextureRegionDrawable(new TextureRegion(cursorTexture));
            cursorDrawable.setMinWidth(3f); // Slightly wider for visibility
            cursorDrawable.setMinHeight(28f); // Slightly taller to match font size
            textFieldStyle.cursor = cursorDrawable;
            System.out.println("Text field textures loaded successfully, cursor size: 3x28");
             */

        } catch (Exception e) {
            System.out.println("Failed to load text field textures: " + e.getMessage());
            textFieldStyle.background = createFallbackNinePatch(new Color(0.2f, 0.2f, 0.2f, 0.9f), "ui/textfield_background.9.png");
            textFieldStyle.cursor = createFallbackCursorTexture();
            System.out.println("Using fallback textures for text field");
        }
        textFieldStyle.selection = createFallbackTexture(new Color(0.29f, 0.56f, 0.89f, 0.4f) ,"selection.png"); // Semi-transparent blue for selection
        skin.add("default", textFieldStyle);
        System.out.println("LabelStyle 'default' added to skin");

        return skin;
    }

    private TextureRegionDrawable createFallbackCursorTexture() {
        System.out.println("Creating fallback cursor texture");
        Pixmap pixmap = new Pixmap(2, 24, Pixmap.Format.RGBA8888); // Narrow, tall cursor
        pixmap.setColor(Color.WHITE);
        pixmap.fillRectangle(0, 0, 10, 24);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        drawable.setMinWidth(2f);
        drawable.setMinHeight(24f);
        return drawable;
    }

    private NinePatchDrawable createFallbackNinePatch(Color color, String assetName) {
        System.out.println("Creating fallback NinePatch for: " + assetName);
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, 64, 64);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        // Define a simple NinePatch with 12-pixel non-stretchable corners
        NinePatch patch = new NinePatch(new TextureRegion(texture), 12, 12, 12, 12);
        return new NinePatchDrawable(patch);
    }


    // MODERN_UI: Helper method to create fallback textures programmatically
    private TextureRegionDrawable createFallbackTexture(Color color, String assetName) {
        System.out.println("Creating fallback texture for: " + assetName);
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, 64, 64);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public void loadAssets() {
        String[] uiAssets = {
            "ui/button.9.png",
            "ui/button_down.9.png",
            "ui/button_over.9.png",
            "ui/textfield_background.9.png",
            "ui/textfield_cursor.png",
            "ui/label_background.9.png"
        };
        for (String asset : uiAssets) {
            try {
                if (Gdx.files.internal(asset).exists()) {
                    assetManager.load(asset, Texture.class);
                    System.out.println("Queued asset: " + asset);
                } else {
                    System.out.println("ERROR: Asset file not found: " + asset);
                    // Optionally create a fallback texture and load it
                    assetManager.load(asset, Texture.class, new TextureLoader.TextureParameter());
                    assetManager.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()) {
                        @Override
                        public Texture loadSync(AssetManager manager, String fileName,  FileHandle file, TextureParameter parameter) {
                            return createFallbackTexture(new Color(0.1f, 0.1f, 0.1f, 1), fileName).getRegion().getTexture();
                        }
                    });
                }
            } catch (Exception e) {
                System.out.println("Error queuing asset " + asset + ": " + e.getMessage());
            }
        }
        if (Gdx.files.internal("ui/button.9.png").exists()) {
            assetManager.load("ui/button.9.png", Texture.class);
        } else {
            System.out.println("WARNING: Button texture not found at: ui/button.9.png");
        }
        if (Gdx.files.internal("ui/button_down.9.png").exists()) {
            assetManager.load("ui/button_down.9.png", Texture.class);
        } else {
            System.out.println("WARNING: Button down texture not found at: ui/button_down.9.png");
        }
        if (Gdx.files.internal("ui/button_over.9.png").exists()) {
            assetManager.load("ui/button_over.9.png", Texture.class);
        } else {
            System.out.println("WARNING: Button over texture not found at: ui/button_over.9.png");
        }
        if (Gdx.files.internal("ui/textfield_background.9.png").exists()) {
            assetManager.load("ui/textfield_background.9.png", Texture.class);
        } else {
            System.out.println("WARNING: Text field background texture not found at: ui/textfield_background.9.png");
        }
        if (Gdx.files.internal("ui/textfield_cursor.png").exists()) {
            assetManager.load("ui/textfield_cursor.png", Texture.class);
        } else {
            System.out.println("WARNING: Text field cursor texture not found at: ui/textfield_cursor.png");
        }
        if (Gdx.files.internal("ui/label_background.9.png").exists()) {
            assetManager.load("ui/label_background.9.png", Texture.class);
        } else {
            System.out.println("WARNING: Label background texture not found at: ui/label_background.9.png");
        }

        //===============


        for (String suit : Deck.SUITS) {
            for (int value = Deck.MIN_VALUE; value <= Deck.MAX_VALUE; value++) {
                Card card = new Card(value, suit);
                String assetPath = card.getImagePath();
                if (Gdx.files.internal(assetPath).exists()) {
                    assetManager.load(assetPath, Texture.class);
                } else {
                    System.out.println("WARNING: Card texture not found at: " + assetPath);
                }
            }
        }
        if (Gdx.files.internal("cards/cardBack.png").exists()) {
            assetManager.load("cards/cardBack.png", Texture.class);
        } else {
            System.out.println("ERROR: Fallback texture not found at: cards/cardBack.png");
        }
        if (Gdx.files.internal("backgroundMain.png").exists()) {
            assetManager.load("backgroundMain.png", Texture.class);
        } else {
            System.out.println("ERROR: Background texture not found at: backgroundMain.png");
        }
    }

    public Texture getCardTexture(Card card) {
        String assetPath = card.getImagePath();
        try {
            if (!assetManager.isLoaded(assetPath)) {
                System.out.println("Texture not loaded for: " + assetPath);
                return assetManager.get("cards/cardBack.png", Texture.class);
            }
            return assetManager.get(assetPath, Texture.class);
        } catch (Exception e) {
            System.out.println("Failed to load card texture: " + assetPath + ". Exception: " + e.getMessage());
            return assetManager.get("cards/cardBack.png", Texture.class);
        }
    }

    public Texture getCardBack() {
        return assetManager.get("cards/cardBack.png", Texture.class);
    }


    public Skin getUISkin() {
        if (skin == null) {
            initializeSkin(); // Lazy initialization
        }
        return skin;
    }

    public Texture getBackgroundTexture() {
        try {
            // FIX: Check if background texture is loaded
            if (!assetManager.isLoaded("backgroundMain.png")) {
                System.out.println("Background texture not loaded");
                return null;
            }
            return assetManager.get("backgroundMain.png", Texture.class);
        } catch (Exception e) {
            System.out.println("Failed to load background texture: " + e.getMessage());
            return null;
        }
    }

    public void finishLoading() {
        assetManager.finishLoading();
        System.out.println("AssetManager finished loading. Loaded assets: " + assetManager.getAssetNames());
    }

    public void dispose() {
        if (skin != null) {
            skin.dispose();
        }
        assetManager.dispose();
    }

    public Drawable load(String s, Class<Texture> textureClass) {
        return (Drawable) assetManager.get(s, textureClass);
    }

    public void debugLoadedAssets() {
        System.out.println("Loaded assets: " + assetManager.getAssetNames());
    }

    public boolean areAssetsLoaded() {
        return assetManager.getProgress() >= 1.0f;
    }
}
