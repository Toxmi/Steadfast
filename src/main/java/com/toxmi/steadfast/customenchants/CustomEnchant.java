package com.toxmi.steadfast.customenchants;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class CustomEnchant {
    protected final CustomManager cm;


    public CustomEnchant() {
        cm = CustomManager.get();
    }


    public abstract void useAbility(Player player, Event event);
}
