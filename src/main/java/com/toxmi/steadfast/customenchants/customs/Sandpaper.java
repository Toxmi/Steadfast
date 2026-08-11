package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Sandpaper extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        assert player != null;
        if (cm.isOnCooldown("sandpaper", player.getUniqueId())) return;

        // Loop through all armor on the player and damage them by 1'
        for (ItemStack item : victim.getInventory().getArmorContents()) {
            if (item == null) continue;
            item.damage(1,player);
        }
        cm.addCooldown("sandpaper", player.getUniqueId());

    }
}
