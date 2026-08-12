package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Reinforce extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof PlayerItemDamageEvent e)) return;
        e.setDamage(e.getDamage() / 2);
    }
}
