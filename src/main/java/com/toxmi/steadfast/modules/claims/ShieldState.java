package com.toxmi.steadfast.modules.claims;

public enum ShieldState {
    INACTIVE("RED"),
    ACTIVE("Green"),
    CHARGING("Aqua"),
    ACTIVATING("Blue"),
    RECHARGING("Yellow");

    ShieldState(String color) {
        this.color = color;
    }
    private final String color;
    public String getColor() {
        return color;
    }
}
