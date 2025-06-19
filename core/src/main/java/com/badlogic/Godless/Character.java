package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;

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
    private float speed = 230   ;
    public int health = 3;
    //Logic
    private Timer timer;
    public Rectangle hurtbox;
    public float size = 2.5f;
    private boolean isMoving = false;
    private boolean isFlipped = false;
    private float elapsedTime = 0;
    private float damCooldown = 0;
    private Gun gun;

    public Character(float x, float y, OrthographicCamera camera){
        // Load textures
        texture = new Texture("Sprites/Players/WandererIdle.png");
        spritesheet = new Texture("Sprites/Players/WandererWalk.png");
        idle = new TextureRegion(texture);

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

        //Walking animation
        walkanimation = new Animation<>(0.1f, animationFrames);
        walkanimation.setPlayMode(Animation.PlayMode.LOOP);

        //position & logic
        position = new Vector2(x, y);
        timer = new Timer();
        hurtbox = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
        gun = new Gun(camera);
    }

    public int getHealth(){
        return health;
    }
    public void update(float delta, ArrayList<Enemy> enemies){
        if (health == 0){
            GameData.Player_Death = true;
            return;
        }

        if (damCooldown > 0){
            damCooldown -= delta;
            GameData.Player_Flee = true;
        }
        else{
            GameData.Player_Flee = false;
        }
        for (Enemy enemy : enemies) {
            if (hurtbox.overlaps(enemy.getHitbox()) && damCooldown <= 0) {
                health -= 1;
                damCooldown = 1f;
            }
        }
        float gunoffsetX = -10f; // Adjust gun placement horizontally
        float gunoffsetY = 25f;  // Adjust gun placement vertically
        gun.update(new Vector2(position.x + gunoffsetX, position.y + gunoffsetY), delta);

        gun.shoot();



        Movement();
        float offsetX = 10f;
        float offsetY = 5f;
        hurtbox.setPosition(position.x + offsetX, position.y + offsetY);
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
    public void render(SpriteBatch batch){
        if (health <= 0) return;
        elapsedTime += Gdx.graphics.getDeltaTime();

        // Select correct frame
        TextureRegion currentframe = isMoving ? walkanimation.getKeyFrame(elapsedTime, true) : new TextureRegion(texture);

        // Draw sprite without calling `batch.begin()` or `batch.end()`
        if (isFlipped) {
            batch.draw(currentframe, position.x + currentframe.getRegionWidth() * size, position.y,
                -currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size);

            // Adjust hurtbox correctly to stay in sync with flipped sprite
            hurtbox.setPosition(position.x + currentframe.getRegionWidth() - hurtbox.width, position.y);
        } else {
            batch.draw(currentframe, position.x, position.y,
                currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size);

            hurtbox.setPosition(position.x, position.y);
        }
        gun.render(batch);
    }

    public void dispose(){
        texture.dispose();
        timer.clear();
    }

    public Vector2 getPosition(){
        return position;
    }

    public Gun getGun() {
        return gun;
    }

    public void reset() {
        health = 3;
    }
}
