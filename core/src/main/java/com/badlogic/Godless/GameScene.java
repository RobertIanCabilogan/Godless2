package com.badlogic.Godless;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Color;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class GameScene implements Screen {

    // Core rendering
    private Game game;
    private Screen gameScreen;
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Stage stage;

    // Assets and audio
    private Texture groundtexture, retryButtonTex, menuButtonTex, youDiedTex;
    private TextureRegion groundregion;
    private Sprite deathTex;
    public Music BGMusic;
    private Sound lvlUp, bgMusic;

    // UI and Fonts
    private BitmapFont font;
    private Hud hud;
    private UpgradeMenu upgradeMenu;
    private ImageButton retryButton, menuButton;

    // Game logic
    private Character character;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullet;
    public EnemySpawner enemySpawner;

    // Timers and flags
    private float scrollx = 0;
    private float timeElapsed = 0;
    private float spawnTimer = 2;
    private float delay = 2.5f;
    private float texDelay = 2f;
    private float upDelay = 1.6f;
    private boolean finalDeath = false;
    private boolean showButtons = false;
    private boolean UpTrigger = false;
    private boolean showDeathTex = false;
    private int NextKillThreshold = 10;

    public GameScene(Game game) {
        // Reset shared state
        GameData.Player_Death = false;
        GameData.kills = 0;

        // Initialize collections and systems
        this.game = game;
        enemies = new ArrayList<>();
        bullet = new ArrayList<>();
        font = new BitmapFont();
        font.getData().setScale(2f);

        camera = new OrthographicCamera();
        character = new Character(100, 100, camera);
        enemySpawner = new EnemySpawner(camera, character);
        hud = new Hud(character, camera);
    }

    private void togglePause() {
        GameData.isPaused = !GameData.isPaused;
        BGMusic.setVolume(GameData.isPaused ? 0.2f : 0.5f);
    }

    @Override
    public void show() {
        UpgradeAssets.load();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        // Load textures
        groundtexture = new Texture("Sprites/World/Ground_1.jpg");
        groundtexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        groundregion = new TextureRegion(groundtexture);
        groundregion.setRegion(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        retryButtonTex = new Texture("Sprites/UI/Retry_Button.png");
        menuButtonTex = new Texture("Sprites/UI/Menu_Button.png");
        youDiedTex = new Texture("Sprites/UI/Death.png");
        deathTex = new Sprite(youDiedTex);

        // Audio setup
        lvlUp = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Level_Up.mp3"));
        BGMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/SFX/BGM.mp3"));
        BGMusic.setLooping(true);
        BGMusic.setVolume(0.5f);
        BGMusic.play();

        // Setup buttons
        retryButton = new ImageButton(new TextureRegionDrawable(retryButtonTex));
        menuButton = new ImageButton(new TextureRegionDrawable(menuButtonTex));
        retryButton.setPosition(150, 150);
        menuButton.setPosition(1000, 150);
        retryButton.setVisible(false);
        menuButton.setVisible(false);
        retryButton.setTouchable(Touchable.disabled);
        menuButton.setTouchable(Touchable.disabled);
        stage.addActor(retryButton);
        stage.addActor(menuButton);

        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScene(game));
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new FirstScreen(game));
            }
        });

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(stage);

        // Upgrade Menu
        upgradeMenu = new UpgradeMenu(camera, new UpgradeSystem(character, character.getGun()));
        stage.addActor(upgradeMenu);
    }

    public void update(float delta) {
        enemySpawner.update(delta, enemies);
        BGMusic.setVolume(GameData.charLvlUp ? 0.2f : 0.6f);

        if (GameData.kills >= NextKillThreshold) {
            if (!UpTrigger) {
                lvlUp.play(0.6f);
                UpTrigger = true;
            }
            upDelay -= delta;
            if (upDelay <= 0) {
                GameData.charLvlUp = true;
                NextKillThreshold = (int) Math.ceil(NextKillThreshold * 1.7);
                GameData.isPaused = true;
                upgradeMenu.setVisible(true);
                upDelay = 1.6f;
                UpTrigger = false;
            }
        }

        if (GameData.isPaused) return;

        if (GameData.Player_Death && !finalDeath) {
            texDelay -= delta;
            if (texDelay <= 0) showDeathTex = true;
            if (showDeathTex) {
                delay -= delta;
                if (delay <= 0) {
                    finalDeath = true;
                    String name = JOptionPane.showInputDialog(null, "Enter Your Name:", "Death", JOptionPane.PLAIN_MESSAGE);
                    if (name == null || name.trim().isEmpty()) name = "[REDACTED]";
                    List<PlayerStats> leaderboard = LeaderboardUtil.loadLeaderboard();
                    leaderboard.add(new PlayerStats(name, GameData.kills, hud.gettimeElapsed()));
                    LeaderboardUtil.saveLeaderboard(leaderboard);
                    new Leaderboard(leaderboard);

                    retryButton.setVisible(true);
                    menuButton.setVisible(true);
                    retryButton.setTouchable(Touchable.enabled);
                    menuButton.setTouchable(Touchable.enabled);
                    BGMusic.dispose();
                }
            }
        }

        if (!GameData.Player_Death) {
            if (spawnTimer <= 0) {
                enemies.addAll(enemySpawner.spawnWave());
                spawnTimer = 2f;
            } else {
                spawnTimer -= delta;
            }
        }

        enemies.removeIf(e -> e.isDead || e.dissapear);
        bullet.removeIf(b -> b.shouldRemove);

        for (Bullet b : bullet) b.update(delta, enemies);
        bullet.addAll(character.getGun().getBullets());
        character.getGun().getBullets().clear();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !GameData.charLvlUp) {
            togglePause();
        }

        update(delta);
        character.update(delta, enemies);
        hud.update(delta);
        timeElapsed += delta;

        for (Enemy enemy : enemies) enemy.update(delta, enemies);

        int minutes = (int) (timeElapsed / 60);
        int seconds = (int) (timeElapsed % 60);
        String time = String.format("%02d:%02d", minutes, seconds);

        camera.position.set(
            character.getPosition().x + character.texture.getWidth() * character.size / 2,
            character.getPosition().y + character.texture.getHeight() * character.size / 2,
            0
        );
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        int tileSize = groundtexture.getWidth();
        float startX = ((int) (camera.position.x / tileSize)) * tileSize - Gdx.graphics.getWidth();
        float startY = ((int) (camera.position.y / tileSize)) * tileSize - Gdx.graphics.getHeight();

        for (float x = startX; x < startX + Gdx.graphics.getWidth() * 2; x += tileSize) {
            for (float y = startY; y < startY + Gdx.graphics.getHeight() * 2; y += tileSize) {
                spriteBatch.draw(groundtexture, x, y, tileSize, tileSize);
            }
        }

        character.render(spriteBatch);
        character.getGun().renderBullets(spriteBatch);
        for (Enemy e : enemies) e.render(spriteBatch);
        for (Bullet b : bullet) b.render(spriteBatch);
        hud.render(spriteBatch);
        spriteBatch.end();

        if (GameData.Player_Death && showDeathTex) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.6f);
            shapeRenderer.rect(
                camera.position.x - camera.viewportWidth / 2f,
                camera.position.y - camera.viewportHeight / 2f,
                camera.viewportWidth,
                camera.viewportHeight
            );
            shapeRenderer.end();
        }

        spriteBatch.begin();
        if (showDeathTex) {
            float x = camera.position.x - deathTex.getWidth() / 2f;
            float y = camera.position.y - deathTex.getHeight() / 2f;
            deathTex.setPosition(x, y);
            deathTex.draw(spriteBatch);
        }
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
        hud.resize(width, height);
        retryButton.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.15f);
        menuButton.setPosition(
            Gdx.graphics.getWidth() * 0.85f - menuButton.getWidth(),
            Gdx.graphics.getHeight() * 0.15f
        );
        upgradeMenu.centerOnCamera(camera);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        BGMusic.dispose();
        shapeRenderer.dispose();
        font.dispose();
        spriteBatch.dispose();
        stage.dispose();
        character.dispose();
    }
}

