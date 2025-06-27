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
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;

public class Character {
    // === Sprites & Animation ===
    public Texture texture;
    private Texture spritesheet;
    private TextureRegion[] animationFrames;
    public TextureRegion idle;
    private Animation<TextureRegion> walkAnimation;
    private float elapsedTime = 0f;
    private boolean isMoving = false;
    private boolean isFlipped = false;

    // === Position & Movement ===
    private Vector2 position;
    private final float offsetX = 38f;
    private final float offsetY = 25f;
    private float speed = 230f;

    // === Combat & Stats ===
    public int health = 3;
    public float size = 2.5f;
    public Rectangle hurtbox;
    private float damCooldown = 0f;

    // === Gameplay Logic ===
    private Gun gun;
    private Timer timer;
    private final int killThreshold = 5;

    // === Audio ===
    private Sound hurt;
    private Sound death;

    // === Constructor ===
    public Character(float x, float y, OrthographicCamera camera) {
        // Load textures
        texture = new Texture("Sprites/Players/WandererIdle(Updated).png");
        spritesheet = new Texture("Sprites/Players/WandererWalk (Updated).png");
        idle = new TextureRegion(texture);

        // Setup animation frames
        int rows = 2;
        int cols = 5;
        int frameWidth = spritesheet.getWidth() / cols;
        int frameHeight = spritesheet.getHeight() / rows;
        TextureRegion[][] temp = TextureRegion.split(spritesheet, frameWidth, frameHeight);

        animationFrames = new TextureRegion[7];
        for (int i = 0; i < 5; i++) animationFrames[i] = temp[0][i];
        animationFrames[5] = temp[1][0];
        animationFrames[6] = temp[1][1];

        walkAnimation = new Animation<>(0.1f, animationFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Load sounds
        hurt = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Hurt.mp3"));
        death = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Death.mp3"));

        // Initialize logic
        position = new Vector2(x, y);
        gun = new Gun(camera);
        timer = new Timer();

        // Initialize hurtbox
        float hurtboxWidth = 25f;
        float hurtboxHeight = 42f;
        hurtbox = new Rectangle(position.x + offsetX, position.y + offsetY, hurtboxWidth, hurtboxHeight);
    }

    // === Update Method ===
    public void update(float delta, ArrayList<Enemy> enemies) {
        if (GameData.isPaused) return;

        // Death check
        if (health <= 0) {
            GameData.Player_Death = true;
            return;
        }

        // Damage cooldown & flee state
        if (damCooldown > 0) {
            damCooldown -= delta;
            GameData.Player_Flee = true;
        } else {
            GameData.Player_Flee = false;
        }

        // Collision damage
        for (Enemy enemy : enemies) {
            if (hurtbox.overlaps(enemy.getHitbox()) && damCooldown <= 0) {
                health -= 1;
                damCooldown = 1f;

                if (health > 0) {
                    hurt.play(1.0f);
                } else {
                    death.play(1.0f);
                }
            }
        }

        // Update gun
        float centerOffsetX = 45f;
        float centerOffsetY = 45f;
        Vector2 playerCenter = new Vector2(position.x + centerOffsetX, position.y + centerOffsetY);
        gun.update(playerCenter, delta);
        gun.shoot();

        // Handle movement
        handleMovement();

        // Update hurtbox
        hurtbox.setPosition(position.x + offsetX, position.y + offsetY);
    }

    // === Movement Logic ===
    private void handleMovement() {
        if (GameData.Player_Death) return;

        float x = 0f, y = 0f;
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed;
            isMoving = true;
            isFlipped = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed;
            isMoving = true;
            isFlipped = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed;
            isMoving = true;
        }

        position.add(x * Gdx.graphics.getDeltaTime(), y * Gdx.graphics.getDeltaTime());
    }

    // === Render Method ===
    public void render(SpriteBatch batch) {
        if (health <= 0) return;

        elapsedTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = isMoving
            ? walkAnimation.getKeyFrame(elapsedTime, true)
            : idle;

        float drawWidth = currentFrame.getRegionWidth() * size;
        float drawHeight = currentFrame.getRegionHeight() * size;

        if (isFlipped) {
            batch.draw(currentFrame,
                position.x + drawWidth, position.y,
                -drawWidth, drawHeight);
        } else {
            batch.draw(currentFrame,
                position.x, position.y,
                drawWidth, drawHeight);
        }

        gun.render(batch);
    }

    // === Utilities ===
    public void dispose() {
        texture.dispose();
        spritesheet.dispose();
        hurt.dispose();
        death.dispose();
        timer.clear();
    }

    public int getHealth() {
        return health;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Gun getGun() {
        return gun;
    }

    public Rectangle getHurtbox() {
        return hurtbox;
    }

    public void applyUpgrade(UpgradeTypes upgrade) {
        switch (upgrade) {
            case EXTRA_HEALTH:
                health += 1;
                break;
            case EXTRA_SPEED:
                speed += 15f;
                break;
        }
    }
}


