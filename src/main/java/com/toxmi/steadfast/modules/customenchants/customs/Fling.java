package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class Fling extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        if (Math.random() > cm.getVar1("fling")) return;
        victim.setVelocity(victim.getVelocity().add(new Vector(0, cm.getVar2("fling"), 0)));
        victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
    }
}
