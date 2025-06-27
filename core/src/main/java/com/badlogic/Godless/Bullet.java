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
    // Core texture and movement
    private Texture bullet;
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 1000f;
    private float angle;

    // Visual and collision
    private float size = 0.5f;
    private final Vector2 hitboxVisualOffset = new Vector2(15f, 12f);
    private Polygon hitbox;

    // Bullet logic
    private float timer = 2f;
    private int damage = 10;
    public boolean shouldRemove = false;

    public Bullet(Vector2 startPos, Vector2 direction, float gunAngle) {
        bullet = new Texture("Sprites/Projectiles/Light Bullet.png");

        position = new Vector2(startPos);
        velocity = new Vector2(direction).scl(speed);
        this.angle = gunAngle;

        float width = 15f;
        float height = 10f;

        // Define centered rectangular hitbox
        float[] vertices = {
            -width / 2f, -height / 2f,
            width / 2f, -height / 2f,
            width / 2f,  height / 2f,
            -width / 2f,  height / 2f
        };

        hitbox = new Polygon(vertices);
        hitbox.setOrigin(0, 0); // Already centered
        Vector2 rotatedOffset = new Vector2(hitboxVisualOffset).rotateDeg(angle);
        hitbox.setPosition(position.x + rotatedOffset.x, position.y + rotatedOffset.y);
        hitbox.setRotation(angle);
    }

    public void update(float delta, ArrayList<Enemy> enemies) {
        // Move bullet
        position.add(velocity.x * delta, velocity.y * delta);

        // Update hitbox
        Vector2 rotatedOffset = new Vector2(hitboxVisualOffset).rotateDeg(angle);
        hitbox.setPosition(position.x + rotatedOffset.x, position.y + rotatedOffset.y);
        hitbox.setRotation(angle);

        // Timer expiration
        timer -= delta;
        if (timer <= 0) {
            shouldRemove = true;
        }

        // Collision detection
        for (Enemy enemy : enemies) {
            if (Intersector.overlapConvexPolygons(hitbox, enemy.getHurtbox())) {
                enemy.takeDamage(damage);
                System.out.println(enemy.Health); // Debugging output
                shouldRemove = true;
                break;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (bullet == null) {
            System.out.println("Skipping rendering: Bullet texture is null!");
            return;
        }

        Vector2 rotatedOffset = new Vector2(hitboxVisualOffset).rotateDeg(angle);
        float drawWidth = bullet.getWidth() * size;
        float drawHeight = bullet.getHeight() * size;

        batch.draw(
            bullet,
            position.x + rotatedOffset.x - drawWidth / 2f,
            position.y + rotatedOffset.y - drawHeight / 2f,
            drawWidth / 2f, drawHeight / 2f, // Origin
            drawWidth, drawHeight,
            1, 1, // Scale
            angle,
            0, 0,
            bullet.getWidth(), bullet.getHeight(),
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
