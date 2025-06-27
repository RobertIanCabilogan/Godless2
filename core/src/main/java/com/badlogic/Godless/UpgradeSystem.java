package com.badlogic.Godless;


public class UpgradeSystem {
    private Character player;
    private Gun gun;

    public UpgradeSystem(Character player, Gun gun){
        this.player = player;
        this.gun = gun;
    }

    public void applyUpgrade(UpgradeTypes type){
        switch (type){
            case EXTRA_HEALTH:
            case EXTRA_SPEED:
                player.applyUpgrade(type);
                break;
            case EXTRA_BULLETS:
            case EXTRA_PROJECTILES:
                gun.applyUpgrade(type);
                break;

        }
    }

}
