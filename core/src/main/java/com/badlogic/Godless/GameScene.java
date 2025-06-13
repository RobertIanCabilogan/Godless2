package com.badlogic.Godless;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class GameScene implements Screen{
    private float timeElapsed = 0;
    private BitmapFont font;
    private GlyphLayout layout;
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
        font = new BitmapFont();
        font.getData().setScale(2f);
        layout = new GlyphLayout();
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
        timeElapsed += delta;

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

        layout.setText(font, time); // Example timer text
        float x = camera.position.x + Gdx.graphics.getWidth() / 2- layout.width - 10; // Align right
        float y = camera.position.y + Gdx.graphics.getHeight() / 2- 20; // Align top
        font.draw(spriteBatch, layout, x, y);

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
        font.dispose();
        spriteBatch.dispose();
        stage.dispose();
        character.dispose();
    }
}
