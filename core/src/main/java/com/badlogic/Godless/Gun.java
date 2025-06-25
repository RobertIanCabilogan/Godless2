package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;

public class Gun {
    private Sound gunshot, reload;
    private Texture guntexture;
    private Vector2 position;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    public int Ammo = 999;
    public int maxAmmo = 15;
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

    public Gun(OrthographicCamera cam) {
        this.camera = cam;
        guntexture = new Texture("Sprites/Guns/Gun_1.png");
        gunshot = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Gunshot.mp3"));
        reload = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Reload.mp3"));
        position = new Vector2();
    }

    public void update(Vector2 playerCenter, float delta) {
        this.position.set(playerCenter);

        Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector3 worldCoords = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
        mousePosition.set(worldCoords.x, worldCoords.y);

        direction.set(mousePosition).sub(playerCenter).nor();
        rotation = direction.angleDeg();

        isFlipped = mousePosition.x < playerCenter.x;

        float offsetDistance = 5f;
        Vector2 offset = new Vector2(offsetDistance, 0).rotateDeg(rotation);
        renderPosition.set(playerCenter).add(offset);

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

        batch.draw(guntexture, position.x - 30, position.y ,
            guntexture.getWidth() / 2f, guntexture.getHeight() / 2f, // Center pivot
            guntexture.getWidth(), guntexture.getHeight(), // Original size
            scale, scale, // Scale down gun size
            angle,
            0, 0, guntexture.getWidth(), guntexture.getHeight(),
            false, isFlipped);
    }

    public void shoot() {
        if (isreloading) return;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && Ammo > 0) {
            // Get mouse position in world coordinates
            Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 worldCoords = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
            mousePosition.set(worldCoords.x, worldCoords.y);

            boolean isFlipped = mousePosition.x < position.x;

            // Offset from the center of the player
            float offsetDistance = 20f; // Distance from center to muzzle
            float angleToMouse = new Vector2(mousePosition).sub(position).angleRad();

            // Compute offset in direction of angle
            Vector2 muzzleOffset = new Vector2(
                (float)Math.cos(angleToMouse) * offsetDistance,
                (float)Math.sin(angleToMouse) * offsetDistance
            );
            Vector2 bulletSpawnPos = new Vector2(position).add(muzzleOffset);

            // Calculate direction from bulletSpawnPos to mouse
            Vector2 bulletDir = new Vector2(mousePosition).sub(bulletSpawnPos).nor();
            float bulletAngle = bulletDir.angleDeg();


            lastBulletSpawn.set(bulletSpawnPos);

            bullets.add(new Bullet(bulletSpawnPos, bulletDir, bulletAngle));
            Ammo--;

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
