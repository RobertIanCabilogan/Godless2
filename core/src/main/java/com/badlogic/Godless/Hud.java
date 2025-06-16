package com.badlogic.Godless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Hud {
    private enum HealthState {
        HEALTHY, INJURED, GRAVELY_INJURED
    }
    private Character player;

    private OrthographicCamera camera;

    private Texture healthyTexture;
    private Texture injuredTexture;
    private Texture graveTexture;

    private TextureRegion[] healthyFrames;
    private TextureRegion[] injuredFrames;
    private TextureRegion[] gravelyFrames;

    private Animation<TextureRegion> healthyAnimation;
    private Animation<TextureRegion> injuredAnimation;
    private Animation<TextureRegion> graveAnimation;

    private HealthState currentState;
    private float elapsedTime = 0;
    private float timeElapsed = 0;
    private boolean isVisible = true;
    private BitmapFont font;

    public Hud(Character player, OrthographicCamera camera){
        this.player = player;
        font = new BitmapFont();
        font.getData().setScale(2f);
        if (camera == null){
            System.out.println("No Camera");
        }
        else{
            this.camera = camera;
        }
        healthyTexture = new Texture("Sprites/UI/Healthy.png");
        injuredTexture = new Texture("Sprites/UI/Injured.png");
        graveTexture = new Texture("Sprites/UI/Wounded.png");

        // for healthysprite;
        int healthyframeWidth = healthyTexture.getWidth() / 5;
        int healthyframeHeight = healthyTexture.getHeight() / 3;
        TextureRegion[][] temphealthy = TextureRegion.split(healthyTexture, healthyframeWidth, healthyframeHeight);
        healthyFrames = new TextureRegion[14];
        healthyFrames[0] = temphealthy[0][0];
        healthyFrames[1] = temphealthy[0][1];
        healthyFrames[2] = temphealthy[0][2];
        healthyFrames[3] = temphealthy[0][3];
        healthyFrames[4] = temphealthy[0][4];
        healthyFrames[5] = temphealthy[1][0];
        healthyFrames[6] = temphealthy[1][1];
        healthyFrames[7] = temphealthy[1][2];
        healthyFrames[8] = temphealthy[1][3];
        healthyFrames[9] = temphealthy[1][4];
        healthyFrames[10] = temphealthy[2][0];
        healthyFrames[11] = temphealthy[2][1];
        healthyFrames[12] = temphealthy[2][2];
        healthyFrames[13] = temphealthy[2][3];

        // for the injured
        int injuredframeWidth = injuredTexture.getWidth() / 5;
        int injuredframeHeight = injuredTexture.getHeight() / 4;
        TextureRegion[][] tempinjured = TextureRegion.split(injuredTexture, injuredframeWidth, injuredframeHeight);
        injuredFrames = new TextureRegion[18];
        injuredFrames[0] = tempinjured[0][0];
        injuredFrames[1] = tempinjured[0][1];
        injuredFrames[2] = tempinjured[0][2];
        injuredFrames[3] = tempinjured[0][3];
        injuredFrames[4] = tempinjured[0][4];
        injuredFrames[5] = tempinjured[1][0];
        injuredFrames[6] = tempinjured[1][1];
        injuredFrames[7] = tempinjured[1][2];
        injuredFrames[8] = tempinjured[1][3];
        injuredFrames[9] = tempinjured[1][4];
        injuredFrames[10] = tempinjured[2][0];
        injuredFrames[11] = tempinjured[2][1];
        injuredFrames[12] = tempinjured[2][2];
        injuredFrames[13] = tempinjured[2][3];
        injuredFrames[14] = tempinjured[2][4];
        injuredFrames[15] = tempinjured[3][0];
        injuredFrames[16] = tempinjured[3][1];
        injuredFrames[17] = tempinjured[3][2];

        int gravelyframeWidth = graveTexture.getWidth() / 5;
        int gravelyframeHeight = graveTexture.getHeight() / 5;
        TextureRegion[][] tempgrave = TextureRegion.split(graveTexture, gravelyframeWidth, gravelyframeHeight);
        gravelyFrames = new TextureRegion[21];
        gravelyFrames[0] = tempgrave[0][0];
        gravelyFrames[1] = tempgrave[0][1];
        gravelyFrames[2] = tempgrave[0][2];
        gravelyFrames[3] = tempgrave[0][3];
        gravelyFrames[4] = tempgrave[0][4];
        gravelyFrames[5] = tempgrave[1][0];
        gravelyFrames[6] = tempgrave[1][1];
        gravelyFrames[7] = tempgrave[1][2];
        gravelyFrames[8] = tempgrave[1][3];
        gravelyFrames[9] = tempgrave[1][4];
        gravelyFrames[10] = tempgrave[2][0];
        gravelyFrames[11] = tempgrave[2][1];
        gravelyFrames[12] = tempgrave[2][3];
        gravelyFrames[13] = tempgrave[2][4];
        gravelyFrames[14] = tempgrave[3][0];
        gravelyFrames[15] = tempgrave[3][1];
        gravelyFrames[16] = tempgrave[3][2];
        gravelyFrames[17] = tempgrave[3][3];
        gravelyFrames[18] = tempgrave[3][4];
        gravelyFrames[19] = tempgrave[4][0];
        gravelyFrames[20] = tempgrave[4][1];

        healthyAnimation = new Animation<>(0.1f, healthyFrames);
        injuredAnimation = new Animation<>(0.1f, injuredFrames);
        graveAnimation = new Animation<>(0.1f, gravelyFrames);

        healthyAnimation.setPlayMode(Animation.PlayMode.LOOP);
        injuredAnimation.setPlayMode(Animation.PlayMode.LOOP);
        graveAnimation.setPlayMode(Animation.PlayMode.LOOP);

        currentState = HealthState.HEALTHY;
    }

    public void update(float delta){
        elapsedTime += delta;
        timeElapsed += delta;
        int playerhealth = player.getHealth();


        isVisible = playerhealth > 0;
        if (playerhealth == 3){
            currentState = HealthState.HEALTHY;
        }
        else if (playerhealth == 2){
            currentState = HealthState.INJURED;
        }
        else if (playerhealth == 1){
            currentState = HealthState.GRAVELY_INJURED;
        }

    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    public void render(SpriteBatch batch){
        elapsedTime += Gdx.graphics.getDeltaTime();
        if (!isVisible) return;
        TextureRegion currentFrame;

        switch (currentState){
            case HEALTHY:
                currentFrame = healthyAnimation.getKeyFrame(elapsedTime, true);
                break;
            case INJURED:
                currentFrame = injuredAnimation.getKeyFrame(elapsedTime, true);
                break;
            case GRAVELY_INJURED:
                currentFrame = graveAnimation.getKeyFrame(elapsedTime, true);
                break;
            default:
                currentFrame = healthyAnimation.getKeyFrame(elapsedTime, true);
        }
        float size = 3f;
        float hudX = camera.position.x - (camera.viewportWidth / 2 - 5);
        float hudY = camera.position.y + (camera.viewportHeight / 2) - 120;
        float scaledWidth = currentFrame.getRegionWidth() * size;
        float scaledHeight = currentFrame.getRegionHeight() * size;
        batch.draw(currentFrame, hudX, hudY, scaledWidth, scaledHeight);

        Gun gun = player.getGun();
        String ammoText = gun.isreloading ? "Reloading..." : gun.Ammo + "/" + gun.maxAmmo;
        font.draw(batch, ammoText, hudX + 10, hudY - 20);



        float timerX = camera.position.x + (camera.viewportWidth / 2) - 100; // Right-aligned
        float timerY = camera.position.y + (camera.viewportHeight / 2) - 30; // Top edge
        int minutes = (int) (timeElapsed / 60);
        int seconds = (int) (timeElapsed % 60);
        String timerText = String.format("%02d:%02d", minutes, seconds);
        font.draw(batch, timerText, timerX, timerY);

    }

    public void dispose(){
        healthyTexture.dispose();
        injuredTexture.dispose();
        graveTexture.dispose();
        font.dispose();
    }
}
