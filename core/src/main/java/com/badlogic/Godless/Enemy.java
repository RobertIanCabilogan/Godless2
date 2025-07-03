package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;

public class Enemy {
    // === Graphics, Animation & Sound ===
    private Texture enemtexture;
    private TextureRegion[] animationFrames;
    private Animation<TextureRegion> walkingAnimation;
    private Sound Death, Hit;

    // === Positioning & Collision ===
    private Vector2 position;
    private Rectangle hitbox;
    private Polygon hurtbox;
    private Rectangle collision;

    // === Gameplay References ===
    private Character player;

    // === Stats ===
    public int Health = 30;
    private final float Speed = 175f;
    private final int damage = 1;
    private final float size = 2f;

    // === State Flags ===
    private float elapsedTime = 0f;
    private boolean isFlipped = false;
    public boolean isDead = false;
    public boolean dissapear = false;
    private float dissapearTime = 6f;
    private final int healthThreshold = 15;

    private final float OffsetX = 19f;
    private final float OffsetY = 10f;

    // === Constructor ===
    public Enemy(float x, float y, Character player) {
        this.player = player;
        enemtexture = new Texture("Sprites/Enemy/BasicMonster.png");
        Death = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Enem_Death.mp3"));
        Hit = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Enem_Hit.mp3"));
        // Walking animation setup
        int rows = 1;
        int cols = 5;
        int frameWidth = enemtexture.getWidth() / cols;
        int frameHeight = enemtexture.getHeight() / rows;

        TextureRegion[][] temp = TextureRegion.split(enemtexture, frameWidth, frameHeight);
        animationFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) animationFrames[i] = temp[0][i];

        walkingAnimation = new Animation<>(0.1f, animationFrames);
        walkingAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Position and Hitboxes
        position = new Vector2(x, y);
        float width = 29f;
        float height = 42f;

        float[] vertices = {
            0, 0,
            width + 15, 0,
            width + 15, height + 15,
            0, height + 15
        };

        hurtbox = new Polygon(vertices);
        hurtbox.setPosition(position.x + OffsetX, position.y + OffsetY);

        hitbox = new Rectangle(position.x + OffsetX, position.y + OffsetY, width, height);
        collision = new Rectangle(position.x + OffsetX, position.y + OffsetY, width, height);
    }

    // === Update ===
    public void update(float delta, ArrayList<Enemy> enemies) {
        if (GameData.isPaused || dissapear) return;

        elapsedTime += delta;

        if (GameData.Player_Death) {
            dissapearTime -= delta;
            if (dissapearTime <= 0) dissapear = true;
        }

        if (!GameData.Player_Death) {
            Vector2 direction;
            if (!GameData.Player_Flee) {
                // Chase player
                direction = new Vector2(player.getPosition()).sub(position).nor();
                isFlipped = player.getPosition().x < position.x;
            } else {
                // Flee from player
                direction = new Vector2(position).sub(player.getPosition()).nor();
                isFlipped = player.getPosition().x > position.x;
            }
            position.add(direction.x * Speed * delta, direction.y * Speed * delta);
        } else {
            // Player dead: flee and stop logic
            GameData.Player_Flee = false;
            Vector2 direction = new Vector2(position).sub(player.getPosition()).nor();
            position.add(direction.x * Speed * delta, direction.y * Speed * delta);
            isFlipped = player.getPosition().x > position.x;
        }

        // Avoid overlapping with other enemies
        for (Enemy other : enemies) {
            if (other != this && collision.overlaps(other.collision)) {
                Vector2 separation = new Vector2(position).sub(other.position).nor();
                position.add(separation.x * Speed * delta * 0.2f, separation.y * Speed * delta * 0.2f);
            }
        }

        // Update hitboxes
        hurtbox.setPosition(position.x + OffsetX, position.y + OffsetY);
        collision.setPosition(position.x + OffsetX, position.y + OffsetY);
        hitbox.setPosition(position.x + OffsetX, position.y + OffsetY);
    }

    // === Render ===
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = walkingAnimation.getKeyFrame(elapsedTime, true);
        float frameWidth = currentFrame.getRegionWidth() * size;
        float frameHeight = currentFrame.getRegionHeight() * size;

        if (!isFlipped) {
            batch.draw(
                currentFrame,
                position.x + frameWidth, position.y,
                -frameWidth, frameHeight
            );
        } else {
            batch.draw(
                currentFrame,
                position.x, position.y,
                frameWidth, frameHeight
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
        Hit.play(0.5f);
        if (Health <= 0) {
            isDead = true;
            GameData.kills += 1;
            Death.play(0.5f);
        }
    }
}
