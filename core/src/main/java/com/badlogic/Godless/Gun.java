package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;

public class Gun {
    private Sound gunshot, reload;
    private Texture guntexture, gunShooting;
    private TextureRegion idleGunRegion;
    private TextureRegion[] shootingFrames;
    private Animation<TextureRegion> gunAnimation;
    private Vector2 position;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    public int Ammo = 15;
    public int maxAmmo = 15;
    public int AddBullet = 1;
    public float RandRange = 5f;
    public float reloadtime = 3.5f;
    public float reloadDuration = 3.5f;
    public boolean isreloading = false;

    private OrthographicCamera camera;
    private float scale = 0.5f;

    private Vector2 renderPosition = new Vector2();
    private Vector2 lastBulletSpawn = new Vector2();
    private Vector2 direction = new Vector2();

    private float rotation = 0f;
    private boolean isFlipped = false;
    private boolean isShooting = false;
    private float shootAnimTimer = 0f;
    private int frameWidth;
    private int frameHeight;

    private final Vector2 muzzleLocalOffset = new Vector2(75f, 25f);

    public Gun(OrthographicCamera cam) {
        this.camera = cam;
        guntexture = new Texture("Sprites/Guns/Gun_1.png");
        gunShooting = new Texture("Sprites/Guns/Gun_1-Sheet.png");
        gunshot = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Gunshot.mp3"));
        reload = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Reload.mp3"));
        position = new Vector2();

        idleGunRegion = new TextureRegion(guntexture);
        int row = 1;
        int col = 2;
        int frameW = gunShooting.getWidth() / col;
        int frameH = gunShooting.getHeight() / row;
        TextureRegion[][] temp = TextureRegion.split(gunShooting, frameW, frameH);
        shootingFrames = new TextureRegion[2];
        shootingFrames[0] = temp[0][0];
        shootingFrames[1] = temp[0][1];

        gunAnimation = new Animation<>(0.1f, shootingFrames);
        frameWidth = frameW;
        frameHeight = frameH;
    }

    public void update(Vector2 playerCenter, float delta) {
        this.position.set(playerCenter);

        Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector3 worldCoords = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
        mousePosition.set(worldCoords.x, worldCoords.y);

        direction.set(mousePosition).sub(playerCenter).nor();
        rotation = direction.angleDeg();

        isFlipped = mousePosition.x < playerCenter.x;

        float pivotDistance = 25f; // distance from player to gun center
        Vector2 pivotOffset = new Vector2(pivotDistance, 0).rotateDeg(rotation);
        renderPosition.set(playerCenter).add(pivotOffset);

        if (isShooting) {
            shootAnimTimer += delta;
            if (gunAnimation.isAnimationFinished(shootAnimTimer)) {
                isShooting = false;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && Ammo < maxAmmo && !isreloading) {
            reload.play(2f);
            isreloading = true;
            reloadtime = reloadDuration;
        }

        if (isreloading) {
            reloadtime -= delta;
            if (reloadtime <= 0) {
                isreloading = false;
                Ammo = maxAmmo;
            }
        }
    }

    public void render(SpriteBatch batch) {
        Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector3 worldcoords = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
        mousePosition.set(worldcoords.x, worldcoords.y);
        boolean isFlipped = Gdx.input.getX() < camera.project(new Vector3(position.x, position.y, 0)).x;
        float angle = new Vector2(mousePosition).sub(position).angleDeg();

        TextureRegion currentFrame = isShooting ? gunAnimation.getKeyFrame(shootAnimTimer, false) : new TextureRegion(idleGunRegion);
        if (currentFrame.isFlipY() != isFlipped) {
            currentFrame.flip(false, true);
        }
        batch.draw(
            currentFrame,
            renderPosition.x - (frameWidth * scale / 2f),
            renderPosition.y - (frameHeight * scale / 2f),
            (frameWidth * scale / 2f), (frameHeight * scale / 2f), // origin for rotation
            frameWidth, frameHeight,
            scale, scale,
            angle
        );
    }

    public void shoot() {
        if (isreloading) return;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && Ammo > 0) {
            // Get mouse position in world coordinates
            Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 worldCoords = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
            mousePosition.set(worldCoords.x, worldCoords.y);

            // Calculate pivoted muzzle position relative to player center
            Vector2 pivotOffset = new Vector2(25f, 0).rotateDeg(rotation); // same as in update()
            Vector2 gunPivotPos = new Vector2(position).add(pivotOffset);

            // Rotate muzzle offset around gun center
            Vector2 rotatedMuzzleOffset = new Vector2(muzzleLocalOffset);
            if (isFlipped) {
                rotatedMuzzleOffset.x = guntexture.getWidth() - muzzleLocalOffset.x * -0.009f;
                rotatedMuzzleOffset.y = guntexture.getWidth() - muzzleLocalOffset.y * 5;
            }

            // Since the gun is scaled and rotates around its center, adjust from texture space
            rotatedMuzzleOffset.scl(scale).sub(guntexture.getWidth() * scale / 2f, guntexture.getHeight() * scale / 2f);
            rotatedMuzzleOffset.rotateDeg(rotation);

            // Final bullet spawn position: from gun pivot + rotated muzzle
            Vector2 bulletSpawnPos = new Vector2(gunPivotPos).add(rotatedMuzzleOffset);
            lastBulletSpawn.set(bulletSpawnPos);

            // Direction from bullet spawn point to mouse
            float baseAngle = new Vector2(mousePosition).sub(bulletSpawnPos).angleDeg();
            float spreadRange = RandRange;

            for (int i = 0; i < AddBullet; i++) {
                float randomAngle = baseAngle + MathUtils.random(-spreadRange, spreadRange);
                Vector2 bulletDir = new Vector2(1, 0).setAngleDeg(randomAngle);

                bullets.add(new Bullet(new Vector2(bulletSpawnPos), bulletDir, randomAngle));
            }
            Ammo--;


            isShooting = true;
            shootAnimTimer = 0f;
            gunshot.play(0.8f);
        } else if (Ammo == 0 && !isreloading) {
            reload.play(2f);
            isreloading = true;
            reloadtime = reloadDuration;
        }
    }


    public void renderBullets(SpriteBatch batch) {
        for (Bullet b : bullets) {
            b.render(batch);
        }
    }

    public Vector2 getLastBulletSpawn() {
        return lastBulletSpawn;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}
