package com.badlogic.Godless;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Enemy {
    private Texture enemtexture;
    private TextureRegion[] animationframes;
    private Animation<TextureRegion> walkinganimation;
    private Rectangle hitbox;
    private Rectangle hurtbox;
    private Rectangle collision;
    private Character player;
    public int Health = 30;
    private float Speed = 175;
    private int damage = 1;
    private float elapsedtime = 0;
    private float size = 2;
    private boolean isflipped = false;
    private Vector2 position;
    public boolean isDead = false;
    private float dissapearTime = 6;
    public boolean dissapear = false;

    public Enemy(float x, float y, Character player){
        this.player = player;

        enemtexture = new Texture("Sprites/Enemy/BasicMonster.png");

        int row = 1;
        int col = 5;
        int frameWidth = enemtexture.getWidth() / col;
        int frameHeight = enemtexture.getHeight() / row;

        TextureRegion[][] temp = TextureRegion.split(enemtexture,frameWidth,frameHeight);
        animationframes = new TextureRegion[3];
        animationframes[0] = temp[0][0];
        animationframes[1] = temp[0][1];
        animationframes[2] = temp[0][2];

        walkinganimation = new Animation<>(0.1f, animationframes);
        walkinganimation.setPlayMode(Animation.PlayMode.LOOP);

        position = new Vector2(x,y);
        float hurtboxWidth = enemtexture.getWidth() - 200;
        float hurtboxHeight = enemtexture.getHeight();

// Center the hurtbox on the sprite
        float hurtboxX = position.x + (enemtexture.getWidth() - hurtboxWidth) / 2f;
        float hurtboxY = position.y + (enemtexture.getHeight() - hurtboxHeight) / 2f;

        hurtbox = new Rectangle(hurtboxX, hurtboxY, hurtboxWidth, hurtboxHeight);

        hitbox = new Rectangle(position.x, position.y, enemtexture.getWidth() - 175, enemtexture.getHeight() - 35);
        collision = new Rectangle(position.x, position.y, enemtexture.getWidth() - 20, enemtexture.getHeight());
    }

    public void update(float delta, ArrayList<Enemy> enemies) {
        if (GameData.isPaused){
            return;
        }
        elapsedtime += delta;
        if (dissapear) return;

        if (GameData.Player_Death){
            dissapearTime -= delta;
            if (dissapearTime <= 0){
                dissapear = true;
            }
        }
        if (!GameData.Player_Death) {
            if (!GameData.Player_Flee) {
                // Normal chase behavior
                Vector2 direction = new Vector2(player.getPosition()).sub(position).nor();
                position.add(direction.x * Speed * delta, direction.y * Speed * delta);
                isflipped = player.getPosition().x < position.x; // Update flip direction
            } else {
                // Flee behavior (only triggered once per hit)
                Vector2 fleeDirection = new Vector2(position).sub(player.getPosition()).nor();
                position.add(fleeDirection.x * Speed * delta, fleeDirection.y * Speed * delta);
                isflipped = player.getPosition().x > position.x; // Ensure proper flipping while fleeing
            }
        } else {
            // When player dies, reset fleeing mode & retreat once
            GameData.Player_Flee = false; // Reset flee state when player is dead

            Vector2 fleeDirection = new Vector2(position).sub(player.getPosition()).nor();
            position.add(fleeDirection.x * Speed * delta, fleeDirection.y * Speed * delta);
            isflipped = player.getPosition().x > position.x; // Proper flip handling
        }

        hurtbox.setPosition(position.x, position.y);
        for (Enemy other : enemies){
            if (other != this && collision.overlaps(other.collision)){
                Vector2 separation = new Vector2(position).sub(other.position).nor();
                position.add(separation.x * Speed * delta * 0.2f, separation.y * Speed * delta * 0.2f);
            }
        }

        collision.setPosition(position.x, position.y);
        hitbox.setPosition(    position.x + (enemtexture.getWidth() - hurtbox.width) / 2f,
            position.y + (enemtexture.getHeight() - hurtbox.height) / 2f
        );
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentframe = walkinganimation.getKeyFrame(elapsedtime, true);
        if (!isflipped) {
            batch.draw(currentframe,
                position.x + currentframe.getRegionWidth() * size, position.y,
                -currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size); // Flip horizontally
        } else {
            batch.draw(currentframe,
                position.x, position.y,
                currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size);
        }
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    public void renderHurtbox(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Color.RED); // Bright red for visibility
        shapeRenderer.rect(hurtbox.x, hurtbox.y, hurtbox.width, hurtbox.height);
    }
    public Rectangle getHurtbox(){
        return hurtbox;
    }

    public void takeDamage(int amount){
        Health -= amount;
        if (Health <= 0){
            isDead = true;
            GameData.kills += 1;
        }
    }

}
