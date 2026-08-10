package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class Fling extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        if (Math.random() > cm.getVar1("fling")) return;
        victim.setVelocity(victim.getVelocity().add(new Vector(0, cm.getVar2("fling"), 0)));

    }
}
