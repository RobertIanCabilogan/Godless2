package com.badlogic.Godless;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.List;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private OrthographicCamera camera;
    private Texture background, title, startTexture, quitTexture, leaderboardTexture, creditsTexture,Dumbass, Kid, back, gifSheet;
    private Image DumbassImage, KidImage;
    private Animation<TextureRegion> skullAnimation;
    private TextureRegion[] gifFrames;
    private float gifTime = 0f;
    private TextureRegion currentframe;
    private Music BGMusiclol;
    private SpriteBatch spriteBatch;
    private Sprite titleSprite;
    private Stage stage;
    private Label RobLabel, KidLable, SkullInfo;
    private ImageButton startbutton, quitbutton, leaderboardButton, creditsButton, backButton;
    private final Game game;
    private boolean showCredits = false;
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
        title = new Texture("Sprites/UI/Title.png");
        startTexture = new Texture("sprites/UI/Start_Button.png");
        quitTexture = new Texture("sprites/UI/Quit_Button.png");
        leaderboardTexture = new Texture("Sprites/UI/Rank_Button.png");
        creditsTexture = new Texture("Sprites/UI/Credits_Sprite.png");
        Dumbass = new Texture("Sprites/Jackasses/Dumbass.jpg");
        Kid = new Texture("Sprites/Jackasses/Kid.jpg");
        back = new Texture("Sprites/UI/Back_Icon.png");
        gifSheet = new Texture("Sprites/Jackasses/Scary_Skull!.png");
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        BGMusiclol = Gdx.audio.newMusic(Gdx.files.internal("Audio/SFX/Funky_Town.mp3"));
        BGMusiclol.setLooping(true);
        BGMusiclol.setVolume(0.5f);

        RobLabel = new Label("Name: Robert Ian E. Cabilogan \n" +
            "Info: Main programmer, Artist, and Composer\n" +
            "Extra Info: The CEO of Coffee. Smells like broken glass ", labelStyle);

        KidLable = new Label("Name: Warlyn Kiddo Ravanes\n" +
            "Info: Main Artist\n" +
            "Extra Info: uhhh - Warlyn 2025", labelStyle);

        SkullInfo = new Label("Sp00kY SKOOL!!!! :o", labelStyle);

        titleSprite = new Sprite(title);
        startbutton = new ImageButton(new TextureRegionDrawable(startTexture));
        quitbutton = new ImageButton(new TextureRegionDrawable(quitTexture));
        leaderboardButton = new ImageButton(new TextureRegionDrawable(leaderboardTexture));
        creditsButton = new ImageButton(new TextureRegionDrawable(creditsTexture));
        backButton = new ImageButton(new TextureRegionDrawable(back));
        DumbassImage = new Image(Dumbass);
        KidImage = new Image(Kid);


        //The Buttons
        titleSprite.setScale(2, 2);
        startbutton.setPosition(125, 125);
        quitbutton.setPosition(1000, 125);
        leaderboardButton.setPosition(200, 125);
        creditsButton.setPosition(600, 125);
        backButton.setPosition(150, 125);
        titleSprite.setPosition((camera.viewportWidth - titleSprite.getWidth()) / 2f, camera.viewportHeight - titleSprite.getHeight() - 100);

        DumbassImage.setPosition(125, 425);
        KidImage.setPosition(950, 100);

        RobLabel.setPosition(350, 500);
        KidLable.setPosition(650, 170);
        SkullInfo.setPosition(350, 375);

        DumbassImage.setSize(200, 200);
        KidImage.setSize(200, 200);

        SkullInfo.setVisible(false);
        KidLable.setVisible(false);
        RobLabel.setVisible(false);
        DumbassImage.setVisible(false);
        KidImage.setVisible(false);

        //Adding the buttons
        stage.addActor(SkullInfo);
        stage.addActor(RobLabel);
        stage.addActor(KidLable);
        stage.addActor(backButton);
        stage.addActor(DumbassImage);
        stage.addActor(KidImage);
        stage.addActor(startbutton);
        stage.addActor(quitbutton);
        stage.addActor(leaderboardButton);
        stage.addActor(creditsButton);

        // SKULL :)
        TextureRegion[][] tmp = TextureRegion.split(gifSheet, gifSheet.getWidth() / 12, gifSheet.getHeight() / 2);
        gifFrames = new TextureRegion[24];
        int index = 0;
        for (int row = 0; row < 2; row++){
            for (int col = 0; col < 12; col++){
                gifFrames[index++] = tmp[row][col];
            }
        }

        skullAnimation = new Animation<TextureRegion>(0.05f, gifFrames);
        gifTime = 0f;

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                showCredits = false;
            }
        });

        creditsButton.addListener(new ClickListener(){
           @Override
            public void clicked(InputEvent event, float x, float y){
               showCredits = true;
           }
        });

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

    public void update(float delta){
        if (showCredits){
            gifTime += delta;
            BGMusiclol.play();
            startbutton.setVisible(false);
            quitbutton.setVisible(false);
            leaderboardButton.setVisible(false);
            creditsButton.setVisible(false);
            titleSprite.setAlpha(0f);
            DumbassImage.setVisible(true);
            KidImage.setVisible(true);
            KidLable.setVisible(true);
            RobLabel.setVisible(true);
            SkullInfo.setVisible(true);
        }
        else{
            BGMusiclol.stop();
            startbutton.setVisible(true);
            quitbutton.setVisible(true);
            leaderboardButton.setVisible(true);
            creditsButton.setVisible(true);
            titleSprite.setAlpha(1f);
            DumbassImage.setVisible(false);
            KidImage.setVisible(false);
            KidLable.setVisible(false);
            RobLabel.setVisible(false);
            SkullInfo.setVisible(false);
        }
    }
    @Override
    public void render(float delta) {
        update(delta);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);
        if (!showCredits) {
            titleSprite.draw(spriteBatch);
        }
        if (showCredits){
            TextureRegion currentFrame = skullAnimation.getKeyFrame(gifTime, true);
            spriteBatch.draw(currentFrame, 540, 290, 200, 200);
        }
        spriteBatch.end();
        stage.act(delta);
        stage.draw();
    }



    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        startbutton.setPosition(125, 125);
        quitbutton.setPosition(stage.getViewport().getWorldWidth() - 250, 125);
        creditsButton.setPosition((stage.getViewport().getWorldWidth() - creditsButton.getWidth()) / 1.6f, 125);
        leaderboardButton.setPosition((stage.getViewport().getWorldWidth() - leaderboardButton.getWidth()) / 2.8f, 125);


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
