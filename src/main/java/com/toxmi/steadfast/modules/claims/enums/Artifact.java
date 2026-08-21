package com.toxmi.steadfast.modules.claims.enums;

public enum Artifact {
    EMPTY("", "");

    private final String name;
    private final String rarity;

     Artifact(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
    }


    public String getName() {
         return name;
    }
}
