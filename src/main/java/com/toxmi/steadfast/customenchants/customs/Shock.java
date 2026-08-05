package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Shock extends CustomEnchant {
    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (cm.isOnCooldown("shock", player.getUniqueId())) return;
        e.setCancelled(true);
        cm.addCooldown("shock", player.getUniqueId());
    }
}
