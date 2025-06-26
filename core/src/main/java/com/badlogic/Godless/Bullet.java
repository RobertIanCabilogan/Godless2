package com.badlogic.Godless;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Bullet {
    private Texture bullet;
    private Vector2 position;
    private Vector2 velocity;
    private float Speed = 1000f;
    private Polygon hitbox;
    public float timer = 2f;
    private int damage = 10;
    private float size = 0.5f;
    public boolean shouldRemove = false;
    private float angle;
    private final Vector2 hitboxVisualOffset = new Vector2(15f, 12f);

    public Bullet(Vector2 startpos, Vector2 direction, float gunAngle) {
        bullet = new Texture("Sprites/Projectiles/Light Bullet.png");
        position = new Vector2(startpos);
        velocity = new Vector2(direction).scl(Speed);
        this.angle = gunAngle;

        float Width = 15f;
        float Height = 10f;

        // Define hitbox centered around (0,0)
        float[] vertices = {
            -Width / 2f, -Height / 2f,
            Width / 2f, -Height / 2f,
            Width / 2f,  Height / 2f,
            -Width / 2f,  Height / 2f
        };

        hitbox = new Polygon(vertices);
        hitbox.setOrigin(0, 0);  // Vertices already centered, origin stays at (0,0)
        Vector2 rotatedOffset = new Vector2(hitboxVisualOffset).rotateDeg(angle);
        hitbox.setPosition(position.x + rotatedOffset.x, position.y + rotatedOffset.y);
        hitbox.setRotation(angle);
    }

    public void update(float delta, ArrayList<Enemy> enemies){
        position.add(velocity.x * delta, velocity.y * delta);
        Vector2 rotatedOffset = new Vector2(hitboxVisualOffset).rotateDeg(angle);
        hitbox.setPosition(position.x + rotatedOffset.x, position.y + rotatedOffset.y);
        hitbox.setRotation(angle);

        timer -= delta;

        if (timer<= 0){
            shouldRemove = true;
        }
        for (Enemy enemy: enemies){
            if (Intersector.overlapConvexPolygons(hitbox, enemy.getHurtbox())) {
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

        Vector2 rotatedOffset = new Vector2(hitboxVisualOffset).rotateDeg(angle);
        float drawWidth = bullet.getWidth() * size;
        float drawHeight = bullet.getHeight() * size;

        batch.draw(
            bullet,
            position.x + rotatedOffset.x - drawWidth / 2f, position.y + rotatedOffset.y - drawHeight / 2f,
            drawWidth / 2f, drawHeight / 2f,
            drawWidth, drawHeight,
            1, 1, angle,
            0, 0, bullet.getWidth(), bullet.getHeight(),
            false, false
        );
    }

    public void renderHitbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.polygon(hitbox.getTransformedVertices());
    }


    public void dispose() {
        if (bullet != null) {
            bullet.dispose();
        }
    }
}
