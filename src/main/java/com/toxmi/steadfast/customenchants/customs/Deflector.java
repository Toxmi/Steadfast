package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Deflector extends CustomEnchant {
    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            e.setCancelled(true);
        }
    }
}
