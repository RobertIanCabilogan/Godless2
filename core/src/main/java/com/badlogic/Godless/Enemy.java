package com.badlogic.Godless;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Enemy {
    // === Graphics & Animation ===
    private Texture enemtexture;
    private TextureRegion[] animationframes;
    private Animation<TextureRegion> walkinganimation;

    // === Positioning & Collision ===
    private Vector2 position;
    private Rectangle hitbox;
    private Polygon hurtbox;
    private Rectangle collision;

    // === Gameplay References ===
    private Character player;

    // === Stats ===
    public int Health = 30;
    private float Speed = 175f;
    private int damage = 1;
    private float size = 2f;

    // === State Flags ===
    private float elapsedtime = 0f;
    private boolean isflipped = false;
    public boolean isDead = false;
    private float dissapearTime = 6f;
    public boolean dissapear = false;
    private int healthThreshhold = 15;

    private float Offsetx = 19f;
    private float Offsety = 10f;

    // === Constructor ===
    public Enemy(float x, float y, Character player) {
        this.player = player;
        enemtexture = new Texture("Sprites/Enemy/BasicMonster.png");

        // Walking animation setup (left untouched)
        int row = 1;
        int col = 5;
        int frameWidth = enemtexture.getWidth() / col;
        int frameHeight = enemtexture.getHeight() / row;
        TextureRegion[][] temp = TextureRegion.split(enemtexture, frameWidth, frameHeight);
        animationframes = new TextureRegion[3];
        animationframes[0] = temp[0][0];
        animationframes[1] = temp[0][1];
        animationframes[2] = temp[0][2];
        walkinganimation = new Animation<>(0.1f, animationframes);
        walkinganimation.setPlayMode(Animation.PlayMode.LOOP);

        // Position
        position = new Vector2(x, y);
        float Width = 29f;
        float Height = 42f;

        // Hitbox & Collision
        float[] vertices = {
            0, 0,            // bottom-left
            Width, 0,        // bottom-right
            Width, Height,   // top-right
            0, Height        // top-left
        };

        hurtbox = new Polygon(vertices);
        hurtbox.setPosition(position.x + Offsetx, position.y + Offsety);
        hitbox = new Rectangle(position.x + Offsetx, position.y + Offsety, Width, Height);
        collision = new Rectangle(position.x + Offsetx, position.y + Offsety, Width, Height);
    }

    // === Update ===
    public void update(float delta, ArrayList<Enemy> enemies) {
        if (GameData.isPaused || dissapear) return;

        elapsedtime += delta;
        if (GameData.Player_Death) {
            dissapearTime -= delta;
            if (dissapearTime <= 0) dissapear = true;
        }

        if (!GameData.Player_Death) {
            if (!GameData.Player_Flee) {
                // Chase player
                Vector2 direction = new Vector2(player.getPosition()).sub(position).nor();
                position.add(direction.x * Speed * delta, direction.y * Speed * delta);
                isflipped = player.getPosition().x < position.x;
            } else {
                // Flee from player
                Vector2 fleeDirection = new Vector2(position).sub(player.getPosition()).nor();
                position.add(fleeDirection.x * Speed * delta, fleeDirection.y * Speed * delta);
                isflipped = player.getPosition().x > position.x;
            }
        } else {
            // Player dead: reset flee & run once
            GameData.Player_Flee = false;
            Vector2 fleeDirection = new Vector2(position).sub(player.getPosition()).nor();
            position.add(fleeDirection.x * Speed * delta, fleeDirection.y * Speed * delta);
            isflipped = player.getPosition().x > position.x;
        }

        // Avoid other enemies
        for (Enemy other : enemies) {
            if (other != this && collision.overlaps(other.collision)) {
                Vector2 separation = new Vector2(position).sub(other.position).nor();
                position.add(separation.x * Speed * delta * 0.2f, separation.y * Speed * delta * 0.2f);
            }
        }

        // Update hitboxes
        hurtbox.setPosition(position.x + Offsetx, position.y + Offsety);
        collision.setPosition(position.x + Offsetx, position.y + Offsety);
        hitbox.setPosition(position.x + Offsetx, position.y + Offsety);
    }

    // === Render ===
    public void render(SpriteBatch batch) {
        TextureRegion currentframe = walkinganimation.getKeyFrame(elapsedtime, true);
        if (!isflipped) {
            batch.draw(
                currentframe,
                position.x + currentframe.getRegionWidth() * size, position.y,
                -currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size
            );
        } else {
            batch.draw(
                currentframe,
                position.x, position.y,
                currentframe.getRegionWidth() * size, currentframe.getRegionHeight() * size
            );
        }
    }

    public void renderHurtbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.polygon(hurtbox.getTransformedVertices());
    }
    public void renderHitbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    // === Accessors ===
    public Rectangle getHitbox() {
        return hitbox;
    }

    public Polygon getHurtbox() {
        return hurtbox;
    }

    // === Combat ===
    public void takeDamage(int amount) {
        Health -= amount;
        if (Health <= 0) {
            isDead = true;
            GameData.kills += 1;
        }
    }
}
