package com.badlogic.Godless;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Bullet {
    private Texture bullet;
    private Vector2 position;
    private Vector2 velocity;
    private float Speed = 1000f;
    private Rectangle hitbox;
    public float timer = 2f;
    private int damage = 10;
    private float size = 0.5f;
    public boolean shouldRemove = false;
    private Gun gun;
    private float angle;



    public Bullet(Vector2 startpos, Vector2 targetpos, float gunAngle){
        bullet = new Texture("Sprites/Projectiles/Light Bullet.png");
        position = new Vector2(startpos);
        velocity = new Vector2(targetpos).sub(position).nor().scl(Speed);
        hitbox = new Rectangle(position.x, position.y, bullet.getWidth(), bullet.getHeight());
        this.angle = gunAngle;
    }

    public void update(float delta, ArrayList<Enemy> enemies){
        hitbox.setPosition(position.x, position.y);
        position.add(velocity.x * delta, velocity.y * delta);
        timer -= delta;

        if (timer<= 0){
            shouldRemove = true;
        }
        for (Enemy enemy: enemies){
            if (hitbox.overlaps(enemy.getHurtbox())) {
                enemy.takeDamage(damage);
                System.out.println(enemy.Health);
                shouldRemove = true;
                break;
            }
        }
    }
    public void render(SpriteBatch batch) {
        if (bullet == null) {
            System.out.println("Skipping rendering: Bullet texture is null!");
            return; // Prevent crashes by skipping rendering
        }

        batch.draw(bullet, position.x, position.y,
            bullet.getWidth() / 2f, bullet.getHeight() / 2f,
            bullet.getWidth() * size, bullet.getHeight() * size,
            1, 1, angle,
            0, 0, bullet.getWidth(), bullet.getHeight(), false, false);
    }



    public void dispose() {
        if (bullet != null) {
            bullet.dispose();
        }
    }
}
