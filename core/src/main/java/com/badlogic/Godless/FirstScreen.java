package com.badlogic.Godless;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.List;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private OrthographicCamera camera;
    private Texture background;
    private SpriteBatch spriteBatch;
    private Sprite titleSprite;
    private Stage stage;
    private ImageButton startbutton, quitbutton, leaderboardButton;
    private final Game game;
    private Screen GameScene;
    public FirstScreen(Game game) {
        this.game = game;
    }


    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        stage = new Stage(new StretchViewport(1280, 720, camera));
        Gdx.input.setInputProcessor(stage);

        background = new Texture("sprites/UI/Background.png");
        Texture title = new Texture("Sprites/UI/Title.png");
        Texture startTexture = new Texture("sprites/UI/Start_Button.png");
        Texture quitTexture = new Texture("sprites/UI/Quit_Button.png");
        Texture leaderboardTexture = new Texture("Sprites/UI/Rank_Button.png");


        titleSprite = new Sprite(title);
        startbutton = new ImageButton(new TextureRegionDrawable(startTexture));
        quitbutton = new ImageButton(new TextureRegionDrawable(quitTexture));
        leaderboardButton = new ImageButton(new TextureRegionDrawable(leaderboardTexture));

        titleSprite.setScale(2, 2);
        startbutton.setPosition(150, 125);
        quitbutton.setPosition(1000, 125);
        leaderboardButton.setPosition(550, 125);
        titleSprite.setPosition((camera.viewportWidth - titleSprite.getWidth()) / 2f, camera.viewportHeight - titleSprite.getHeight() - 100);

        stage.addActor(startbutton);
        stage.addActor(quitbutton);
        stage.addActor(leaderboardButton);

        leaderboardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                List<PlayerStats> leaderboard = LeaderboardUtil.loadLeaderboard();
                new Leaderboard(leaderboard);
            }
        });
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
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);
        titleSprite.draw(spriteBatch);
        spriteBatch.end();
        stage.act(delta);
        stage.draw();
    }



    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        startbutton.setPosition(150, 125);
        quitbutton.setPosition(stage.getViewport().getWorldWidth() - 250, 125);
        leaderboardButton.setPosition((stage.getViewport().getWorldWidth() - leaderboardButton.getWidth()) / 2f, 125);


        // Recalculate button positions based on new screen size

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
    }
}
