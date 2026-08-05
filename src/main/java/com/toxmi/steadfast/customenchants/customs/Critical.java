package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Critical extends CustomEnchant {
    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!e.isCritical()) return;
        if (Math.random() > cm.getVar1("critical")) return;
        e.setDamage(e.getDamage() * 2.0);

    }
}
