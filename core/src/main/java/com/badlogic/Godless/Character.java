package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Character {
    //Sprites
    public Texture texture;
    private Texture spritesheet;
    private TextureRegion[] animationFrames;
    public TextureRegion idle;
    private Animation<TextureRegion> walkanimation;
    //Movement
    private Vector2 position;
    //Stats
    private float speed = 250;
    private int health = 3;
    //Logic
    private Timer timer;
    private Rectangle hurtbox;
    public float size = 2.5f;
    private boolean isMoving = false;
    private boolean isFlipped = false;
    private float elapsedTime = 0;

    public Character(float x, float y){
        // Load textures
        texture = new Texture("Sprites/Players/WandererIdle.png");
        spritesheet = new Texture("Sprites/Players/WandererWalk.png");
        idle = new TextureRegion(texture); // Store the idle texture once


        int row = 2;
        int col = 5;
        int frameWidth = spritesheet.getWidth() / col;
        int frameHeight = spritesheet.getHeight() / row;


        TextureRegion[][] temp = TextureRegion.split(spritesheet, frameWidth, frameHeight);


        animationFrames = new TextureRegion[7];
        animationFrames[0] = temp[0][0];
        animationFrames[1] = temp[0][1];
        animationFrames[2] = temp[0][2];
        animationFrames[3] = temp[0][3];
        animationFrames[4] = temp[0][4];
        animationFrames[5] = temp[1][0];
        animationFrames[6] = temp[1][1];

        // Create walking animation
        walkanimation = new Animation<>(0.1f, animationFrames);
        walkanimation.setPlayMode(Animation.PlayMode.LOOP);

        // Initialize position & logic
        position = new Vector2(x, y);
        timer = new Timer();
        hurtbox = new Rectangle(position.x, position.y, texture.getWidth() - 1, texture.getHeight() - 1);
    }



    public void update(float delta){
        if (health == 0){
            GameData.Player_Death = true;
        }
        Movement();
        hurtbox.setPosition(position.x, position.y);
    }
    public void checkCollision(Rectangle otherObject){

    }
    private void Movement(){
        if (!GameData.Player_Death){
            float x = 0, y = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.A)){
                x -= speed;
                isMoving = true;
                isFlipped = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)){
                x += speed;
                isMoving = true;
                isFlipped = false;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)){
                y += speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)){
                y -= speed;
            }

            if (x == 0 && y == 0){
                isMoving = false;
            }

            position.add(x * Gdx.graphics.getDeltaTime(), y * Gdx.graphics.getDeltaTime());
        }
    }
    private void startTimer(float delay){
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                GameData.Player_Flee = false;
            }
        };
    }
    public void render(SpriteBatch batch){
        elapsedTime += Gdx.graphics.getDeltaTime();

        // Select correct frame
        TextureRegion currentframe = isMoving ? walkanimation.getKeyFrame(elapsedTime, true) : new TextureRegion(texture);

        // Draw sprite without calling `batch.begin()` or `batch.end()`
        if (isFlipped){
            batch.draw(currentframe, position.x + currentframe.getRegionWidth() * size, position.y,
                -currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size);
        } else {
            batch.draw(currentframe, position.x, position.y,
                currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size);
        }
    }


    public void dispose(){
        texture.dispose();
        timer.clear();
    }

    public Vector2 getPosition(){
        return position;
    }
}
