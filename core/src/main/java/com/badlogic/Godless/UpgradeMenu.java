package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UpgradeMenu extends Group {
    private final float width = 400;
    private final float height = 200;
    private final float scale = 1.9f;
    private final int spacing = 95;
    private final int buttonSize = 80;

    private Texture topFrame;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final OrthographicCamera camera;
    private final UpgradeSystem upgradeSystem;

    public UpgradeMenu(OrthographicCamera camera, UpgradeSystem upgradeSystem) {
        this.camera = camera;
        this.upgradeSystem = upgradeSystem;

        // Label setup
        BitmapFont font = new BitmapFont(); // Default font
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label titleLabel = new Label("Choose Your Boon", labelStyle);
        titleLabel.setFontScale(2.3f);
        titleLabel.setPosition((width / 2f - titleLabel.getPrefWidth() / 2f) + 150, height - 70);
        addActor(titleLabel);

        // Center this group in the camera
        camera.update();
        float centerX = camera.position.x - width / 2f + 250;
        float centerY = camera.position.y - height / 2f + 45;
        setBounds(centerX, centerY, width, height);

        // Create upgrade buttons
        UpgradeTypes[] upgrades = UpgradeTypes.values();

        for (int i = 0; i < upgrades.length; i++) {
            UpgradeTypes type = upgrades[i];

            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = new TextureRegionDrawable(new TextureRegion(UpgradeAssets.getIcon(type)));

            ImageButton button = new ImageButton(style);
            button.setSize(buttonSize * scale, buttonSize * scale);
            button.setPosition(i * (buttonSize + spacing) + 20, height / 2f - buttonSize / 2f - 60);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    upgradeSystem.applyUpgrade(type);
                    UpgradeMenu.this.setVisible(false);
                    GameData.isPaused = false;
                    GameData.charLvlUp = false;
                }
            });

            addActor(button);
        }

        topFrame = new Texture("Sprites/UI/UpgradeFrame.png");
        setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Background rectangle
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        shapeRenderer.rect(getX(), getY(), width * scale - 40, height);
        shapeRenderer.end();

        batch.begin();

        // Frame texture
        float scaledWidth = topFrame.getWidth() * scale;
        float scaledHeight = topFrame.getHeight() * scale;
        batch.draw(topFrame, getX() - 20, getY() + height - scaledHeight + 113, scaledWidth, scaledHeight);

        super.draw(batch, parentAlpha); // Draw buttons and label
    }

    public void centerOnCamera(OrthographicCamera camera) {
        float centerX = camera.position.x - getWidth() / 2f;
        float centerY = camera.position.y - getHeight() / 2f;
        setPosition(centerX, centerY);
    }
}
