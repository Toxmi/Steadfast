package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

public class Slash extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        Entity victim = e.getEntity();
        for (LivingEntity le : victim.getLocation().getNearbyLivingEntities(cm.getVar1("slash"))) {
            if (le.equals(player) || le.equals(victim)) continue;
            le.damage(e.getDamage());
        }
    }
}
