package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class Gun {
        private Texture guntexture;
        private Vector2 position;
        private ArrayList<Bullet> bullets = new ArrayList<>();
        public int Ammo = 15;
        public int maxAmmo = 15;
        public float reloadtime = 3;
        public float reloadDuration = 3;
        public boolean isreloading = false;
        private OrthographicCamera camera;
        private float scale = 0.5f;
        private Vector2 adjustedPosition = new Vector2();


    public Gun(OrthographicCamera cam){
            this.camera = cam;
            guntexture = new Texture("Sprites/Guns/Gun_1.png");
            position = new Vector2();
        }
        public void update(Vector2 playerPosition, float delta){
            adjustedPosition.set(playerPosition);
            position.set(playerPosition.x, playerPosition.y);

            if (Gdx.input.isKeyJustPressed(Input.Keys.R) && Ammo < maxAmmo && !isreloading) {
                isreloading = true;
                reloadtime = reloadDuration; // Reset reload timer
            }

            if (isreloading){
                reloadtime -= delta;
                if (reloadtime <= 0){
                    isreloading = false;
                    Ammo = maxAmmo;
                }
            }
        }
        public void render(SpriteBatch batch){
            Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 worldcoords = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
            mousePosition.set(worldcoords.x, worldcoords.y);
            boolean isFlipped = Gdx.input.getX() < camera.project(new Vector3(position.x, position.y, 0)).x;
            float angle = new Vector2(mousePosition).sub(position).angleDeg();

            batch.draw(guntexture, position.x, position.y,
                guntexture.getWidth() / 2f, guntexture.getHeight() / 2f, // Center pivot
                guntexture.getWidth(), guntexture.getHeight(), // Original size
                scale, scale, // Scale down gun size
                angle,
                0, 0, guntexture.getWidth(), guntexture.getHeight(),
                false, isFlipped);


        }
    public void shoot() {
        if (isreloading) return;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && Ammo > 0 && !isreloading) {
            Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 worldCoords = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
            mousePosition.set(worldCoords.x, worldCoords.y);

            Vector2 direction = new Vector2(mousePosition).sub(adjustedPosition).nor();
            float gunAngle = direction.angleDeg();

            float barrelDistance = guntexture.getWidth() * scale / 2f;
            float verticalOffset =  50.5f * scale;

            Vector2 localOffset = new Vector2(barrelDistance, verticalOffset);
            Vector2 barrelOffset = localOffset.rotateDeg(gunAngle);
            boolean isFlipped = Gdx.input.getX() < camera.project(new Vector3(adjustedPosition.x, adjustedPosition.y, 0)).x;
            if (isFlipped) {
                barrelOffset.x *= -1;
            }
            Vector2 bulletSpawnPos = new Vector2(adjustedPosition).add(barrelOffset);

            bullets.add(new Bullet(bulletSpawnPos, direction, gunAngle));
            Ammo -= 1;
            System.out.println(Ammo);
        }
        else if (Ammo == 0 && !isreloading){
            isreloading = true;
            reloadtime = reloadDuration;
        }
    }

    public ArrayList<Bullet> getBullets() { return bullets; }
    public void renderBullets(SpriteBatch batch) {
        for (Bullet b : bullets) {
            b.render(batch);
        }
    }
}
