package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

public class Shock extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        assert player != null;
        if (cm.isOnCooldown("shock", player.getUniqueId())) return;
        e.setCancelled(true);
        cm.addCooldown("shock", player.getUniqueId());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
    }
}
