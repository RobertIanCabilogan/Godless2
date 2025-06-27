package com.badlogic.Godless;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.Random;
import com.badlogic.Godless.Character;
import com.badlogic.Godless.Enemy;

public class EnemySpawner {
    private OrthographicCamera camera;
    private Character target;
    private Random random = new Random();

    private int enemiesPerSpawn = 1;
    private float timeElapsed = 0f;
    private float nextTimeThreshold = 15f;

    public EnemySpawner(OrthographicCamera camera, Character target) {
        this.camera = camera;
        this.target = target;
    }

    public Enemy spawn() {
        float buffer = 100f; // Distance outside the screen to spawn from
        float spawnX = 0f, spawnY = 0f;

        int edge = random.nextInt(4); // 0 = top, 1 = bottom, 2 = left, 3 = right

        switch (edge) {
            case 0: // Top
                spawnX = camera.position.x + random.nextFloat() * camera.viewportWidth - (camera.viewportWidth / 2);
                spawnY = camera.position.y + (camera.viewportHeight / 2) + buffer;
                break;

            case 1: // Bottom
                spawnX = camera.position.x + random.nextFloat() * camera.viewportWidth - (camera.viewportWidth / 2);
                spawnY = camera.position.y - (camera.viewportHeight / 2) - buffer;
                break;

            case 2: // Left
                spawnX = camera.position.x - (camera.viewportWidth / 2) - buffer;
                spawnY = camera.position.y + random.nextFloat() * camera.viewportHeight - (camera.viewportHeight / 2);
                break;

            case 3: // Right
                spawnX = camera.position.x + (camera.viewportWidth / 2) + buffer;
                spawnY = camera.position.y + random.nextFloat() * camera.viewportHeight - (camera.viewportHeight / 2);
                break;
        }

        System.out.println("Enemy Spawned at: " + spawnX + " , " + spawnY);
        return new Enemy(spawnX, spawnY, target);
    }

    public List<Enemy> spawnWave() {
        if (GameData.Player_Death) return new ArrayList<>();

        List<Enemy> newEnemies = new ArrayList<>();
        for (int i = 0; i < enemiesPerSpawn; i++) {
            newEnemies.add(spawn());
            System.out.println("Spawned enemy!");
        }

        return newEnemies;
    }

    public void update(float delta, ArrayList<Enemy> enemies) {
        if (GameData.Player_Death) return;
        if (GameData.isPaused) return;

        timeElapsed += delta;

        if (timeElapsed >= nextTimeThreshold) {
            enemiesPerSpawn += 1;
            nextTimeThreshold += 15f;

            for (Enemy e : enemies) {
                e.Health += 15;
                System.out.println("Survived 30s! Enemies per spawn: " + enemiesPerSpawn);
            }
        }
    }
}
