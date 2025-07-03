package com.badlogic.Godless;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Cutscene implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private Texture cutsceneImage;
    private OrthographicCamera camera;
    private Viewport viewport;
    private float timer = 0f;
    private float alpha = 0f;
    private Sound StartSound;
    private enum State { BLACK, FADE_IN, SHOW, FADE_OUT, DONE }
    private State state = State.BLACK;

    public Cutscene(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        viewport = new FitViewport(1280, 720, camera);
        cutsceneImage = new Texture("Sprites/Splash/Silly.png");
        StartSound = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Intro_Startup.wav"));
    }

    @Override
    public void render(float delta) {
        timer += delta;

        switch (state) {
            case BLACK:
                if (timer > 0.5f) {
                    state = State.FADE_IN;
                    timer = 0f;
                }
                break;
            case FADE_IN:
                alpha = Math.min(1f, timer / 1.5f);
                if (alpha >= 1f) {
                    state = State.SHOW;
                    timer = 0f;
                    StartSound.play(0.5f);
                }
                break;
            case SHOW:
                if (timer > 1.2f) {
                    state = State.FADE_OUT;
                    timer = 0f;

                }
                break;
            case FADE_OUT:
                alpha = Math.max(0f, 1f - timer / 1.5f);
                if (alpha <= 0f) {
                    state = State.DONE;
                }
                break;
            case DONE:
                game.setScreen(new FirstScreen(game));
                dispose(); // clean up
                return;
        }

        // Clear the screen to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        Color oldColor = batch.getColor();
        batch.setColor(1, 1, 1, alpha);

        // Draw image centered and scaled to original size
        float x = (viewport.getWorldWidth() - cutsceneImage.getWidth()) / 2f;
        float y = (viewport.getWorldHeight() - cutsceneImage.getHeight()) / 2f;
        batch.draw(cutsceneImage, x, y);
        batch.setColor(oldColor);
        batch.end();
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        cutsceneImage.dispose();
    }
}
