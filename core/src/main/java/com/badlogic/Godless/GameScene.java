package com.badlogic.Godless;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import org.w3c.dom.Text;
import com.badlogic.Godless.PlayerStats;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class GameScene implements Screen{
    //Core rendering
    private Game game;
    private Screen gameScreen;
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    //The Textures
    private Texture groundtexture, retryButtonTex, menuButtonTex;
    private TextureRegion groundregion;
    private float scrollx = 0;
    //Actors
    private Character character;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullet;
    public EnemySpawner enemySpawner;
    //UI
    private Hud hud;
    private BitmapFont font;
    private float timeElapsed = 0;
    private ImageButton retryButton, menuButton;
    //Logic
    private float spawnTimer = 2;
    private float delay = 2.5f;
    private boolean finalDeath = false;
    private boolean showButtons = false;

    public GameScene(Game game){
        // Reset shared state
        GameData.Player_Death = false;
        GameData.kills = 0;

        // Clear and reinitialize everything
        enemies = new ArrayList<>();
        bullet = new ArrayList<>();

        camera = new OrthographicCamera(); // Or reuse if already created
        character = new Character(100, 100, camera);
        enemySpawner = new EnemySpawner(camera, character);
        hud = new Hud(character, camera);

        spawnTimer = 2f;
        finalDeath = false;
        delay = 2f;


        this.game = game;
        font = new BitmapFont();
        font.getData().setScale(2f);
        bullet = new ArrayList<Bullet>();
    }

    @Override
    public void show(){
        enemySpawner = new EnemySpawner(camera, character);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        groundtexture = new Texture("Sprites/World/Ground_1.jpg");
        groundtexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        retryButtonTex = new Texture("Sprites/UI/Retry_Button.png");
        menuButtonTex = new Texture("Sprites/UI/Menu_Button.png");

        retryButton = new ImageButton(new TextureRegionDrawable(retryButtonTex));
        menuButton = new ImageButton(new TextureRegionDrawable(menuButtonTex));

        retryButton.setPosition(150, 150);
        menuButton.setPosition(1000, 150);

        retryButton.setVisible(false);
        menuButton.setVisible(false);

        stage.addActor(retryButton);
        stage.addActor(menuButton);

        retryButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new GameScene(game));
            }
        });

        menuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new FirstScreen(game));
            }
        });
        retryButton.setTouchable(Touchable.disabled);
        menuButton.setTouchable(Touchable.disabled);

        groundregion = new TextureRegion(groundtexture);
        groundregion.setRegion(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.input.setInputProcessor(stage);
    }
    public void update(float delta){
        enemySpawner.update(delta);
        if(GameData.Player_Death && !finalDeath){
            delay -= delta;
            if (delay <= 0){
                finalDeath = true;
                String name = JOptionPane.showInputDialog(null, "Enter Your Name:", "Death", JOptionPane.PLAIN_MESSAGE);
                if (name == null || name.trim().isEmpty()) name = "[REDACTED]";

                int kills = GameData.kills;
                float time = hud.gettimeElapsed();

                List<PlayerStats> leaderboard = LeaderboardUtil.loadLeaderboard();
                leaderboard.add(new PlayerStats(name, kills, time));
                LeaderboardUtil.saveLeaderboard(leaderboard);
                new Leaderboard(leaderboard);

                retryButton.setVisible(true);
                retryButton.setTouchable(Touchable.enabled);
                menuButton.setVisible(true);
                menuButton.setTouchable(Touchable.enabled);
            }
        }
        if (!GameData.Player_Death && spawnTimer <= 0) {
            enemies.addAll(enemySpawner.spawnWave());
            spawnTimer = 2f;
        } else {
            spawnTimer -= delta;
        }

        // this is for game updates.
        enemies.removeIf(enemy -> enemy.isDead || enemy.dissapear);
        bullet.removeIf(b -> b.shouldRemove);
        for (Bullet bullet : bullet) {
            bullet.update(delta, enemies);
        }
        bullet.addAll(character.getGun().getBullets());
        character.getGun().getBullets().clear();
    }

    @Override
    public void render(float delta){
        update(delta);
        character.update(delta, enemies);

        hud.update(delta);
        timeElapsed += delta;
        for (Enemy enemy : enemies){
            enemy.update(delta, enemies);
        }

        int minutes = (int) (timeElapsed / 60);
        int seconds = (int) (timeElapsed % 60);
        String time = String.format("%02d:%02d", minutes, seconds);

        camera.position.set(character.getPosition().x + character.texture.getWidth() * character.size / 2,
            character.getPosition().y + character.texture.getHeight() * character.size / 2, 0);
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
        //character
        character.render(spriteBatch);
        character.getGun().renderBullets(spriteBatch);
        for (Enemy enemy : enemies){
            enemy. render(spriteBatch);
        }

        hud.render(spriteBatch);
        for (Bullet b : bullet) {
            b.render(spriteBatch);
        }
        spriteBatch.end();
        stage.act(delta);
        stage.draw();
    }
    // this is for drawing enemies
    @Override
    public void resize(int width, int height){
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
        hud.resize(width, height);
        retryButton.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.15f);
        menuButton.setPosition(Gdx.graphics.getWidth() * 0.85f - menuButton.getWidth(), Gdx.graphics.getHeight() * 0.15f);
    }

    @Override
    public void pause(){
    }
    @Override
    public void resume(){
    }
    @Override
    public void hide(){
    }

    @Override
    public void dispose(){
        shapeRenderer.dispose();
        font.dispose();
        spriteBatch.dispose();
        stage.dispose();
        character.dispose();
    }
}
