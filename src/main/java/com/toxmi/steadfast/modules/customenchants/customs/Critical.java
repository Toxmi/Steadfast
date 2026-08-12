package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

public class Critical extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!e.isCritical()) return;
        if (Math.random() > cm.getVar1("critical")) return;
        e.setDamage(e.getDamage() * 2.0);

    }
}
