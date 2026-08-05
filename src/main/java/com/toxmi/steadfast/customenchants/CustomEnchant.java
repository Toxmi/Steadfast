package com.toxmi.steadfast.customenchants;

import com.toxmi.steadfast.Steadfast;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class CustomEnchant {
    protected final CustomManager cm;
    protected final Steadfast plugin;

    public CustomEnchant() {
        cm = CustomManager.get();
        plugin = Steadfast.get();
    }


    public abstract void useAbility(Player player, Event event);
}
