package com.badlogic.Godless;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;


public class GameScene implements Screen{
    private Texture groundtexture;
    private TextureRegion groundregion;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private Game game;
    private Screen gameScreen;
    private float scrollx = 0;
    private Character character;
    private OrthographicCamera camera;


    public GameScene(Game game){
        this.game = game;
    }

    @Override
    public void show(){
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        groundtexture = new Texture("Sprites/World/Ground_1.jpg");
        groundtexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        groundregion = new TextureRegion(groundtexture);
        groundregion.setRegion(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new OrthographicCamera();
        character = new Character(100, 100);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta){
        character.update(delta);

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

        // Render the character without its own begin()/end()
        character.render(spriteBatch);

        spriteBatch.end(); // Ensure `end()` is properly called
        stage.act(delta);
        stage.draw();
    }



    @Override
    public void resize(int width, int height){
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
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
        spriteBatch.dispose();
        stage.dispose();
        character.dispose();
    }
}
