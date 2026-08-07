package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

public class LowGround extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (e.getDamager().getLocation().getBlockY() - 0.25 < e.getEntity().getLocation().getBlockY()) {
            e.setDamage(e.getDamage() * cm.getVar1("lowground"));
        }
    }
}
