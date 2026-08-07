package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.jetbrains.annotations.Nullable;

public class Heavenly extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityResurrectEvent e)) return;
        if (cm.isOnCooldown("heavenly", player.getUniqueId())) return;

    }
}
