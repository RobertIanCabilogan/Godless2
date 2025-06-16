package com.badlogic.Godless;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.ArrayList;

public class GameScene implements Screen{
    private ShapeRenderer shapeRenderer;
    private float timeElapsed = 0;
    private BitmapFont font;
    private Texture groundtexture;
    private TextureRegion groundregion;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private Game game;
    private Screen gameScreen;
    private float scrollx = 0;
    private Character character;
    private OrthographicCamera camera;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullet;
    private Hud hud;

    public GameScene(Game game){
        this.game = game;
        font = new BitmapFont();
        font.getData().setScale(2f);
        bullet = new ArrayList<Bullet>();
    }

    @Override
    public void show(){
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        groundtexture = new Texture("Sprites/World/Ground_1.jpg");
        groundtexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        groundregion = new TextureRegion(groundtexture);
        groundregion.setRegion(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new OrthographicCamera();
        character = new Character(100, 100, camera);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        enemies = new ArrayList<>();
        enemies.add(new Enemy(200, 300, character));
        enemies.add(new Enemy(300, 400, character));
        enemies.add(new Enemy(100, 500, character));
        bullet = new ArrayList<>();
        hud = new Hud(character, camera);
        Gdx.input.setInputProcessor(stage);
    }
    public void update(float delta){
        enemies.removeIf(enemy -> enemy.isDead);
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
        spriteBatch.end(); // Ensure `end()` is properly called
        stage.act(delta);
        stage.draw();
    }
    // this is for drawing enemies
    @Override
    public void resize(int width, int height){
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
        hud.resize(width, height);
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
