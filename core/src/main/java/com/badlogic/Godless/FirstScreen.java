package com.badlogic.Godless;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private Texture background;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private ImageButton startbutton, quitbutton;
    private final Game game;
    private Screen GameScene;
    public FirstScreen(Game game) {
        this.game = game;
    }


    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture("sprites/UI/Background.png");
        Texture startTexture = new Texture("sprites/UI/Start_Button.png");
        Texture quitTexture = new Texture("sprites/UI/Quit_Button.png");


        startbutton = new ImageButton(new TextureRegionDrawable(startTexture));
        quitbutton = new ImageButton(new TextureRegionDrawable(quitTexture));

        startbutton.setPosition(150, 150);
        quitbutton.setPosition(1000, 100);

        stage.addActor(startbutton);
        stage.addActor(quitbutton);

        startbutton.addListener(new ClickListener(){
            @Override
           public void clicked(InputEvent event, float x, float y){
                game.setScreen(new GameScene(game));
               System.out.println("Start!");
           }
        });

        quitbutton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
        // Draw your screen here. "delta" is the time since last render in seconds.
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);


        // Recalculate button positions based on new screen size
        startbutton.setPosition(width * 0.1f, height * 0.1f);  // 10% from left, 10% from bottom
        quitbutton.setPosition(width * 0.8f, height * 0.1f);   // 80% from left,
        // Resize your screen here. The parameters represent the new window size.

    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        background.dispose();
        stage.dispose();
        background.dispose();
        // Destroy screen's assets here.
    }
}
