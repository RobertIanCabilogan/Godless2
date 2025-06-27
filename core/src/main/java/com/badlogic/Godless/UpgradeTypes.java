package com.badlogic.Godless;

public enum UpgradeTypes {
    EXTRA_BULLETS("More Bullets"),
    EXTRA_HEALTH("More Health"),
    EXTRA_PROJECTILES("More Projectiles"),
    EXTRA_SPEED("More Speed");

    public final String displayName;

    UpgradeTypes(String displayName){
        this.displayName = displayName;
    }
}
