package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Weightless extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            e.setCancelled(true);
            assert player != null;
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_LAND, 1.0f, 1.0f);
        }
    }
}
