package com.badlogic.Godless;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class UpgradeAssets {
    public static final HashMap<UpgradeTypes, Texture> upgradeIcons = new HashMap<>();

    public static void load(){
        upgradeIcons.put(UpgradeTypes.EXTRA_BULLETS, new Texture("Sprites/UI/Amo_Upgrade.png"));
        upgradeIcons.put(UpgradeTypes.EXTRA_PROJECTILES, new Texture("Sprites/UI/Proj_Upgrade.png"));
        upgradeIcons.put(UpgradeTypes.EXTRA_HEALTH, new Texture("Sprites/UI/Health_Upgrade.png"));
        upgradeIcons.put(UpgradeTypes.EXTRA_SPEED, new Texture("Sprites/UI/Spd_Upgrade.png"));
    }

    public static Texture getIcon(UpgradeTypes type){
        return upgradeIcons.get(type);
    }
}
