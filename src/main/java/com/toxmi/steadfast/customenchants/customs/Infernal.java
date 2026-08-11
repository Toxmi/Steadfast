package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.Nullable;

public class Infernal extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityShootBowEvent e)) return;
        assert player != null;
        if (cm.isOnCooldown("infernal", player.getUniqueId())) return;
        e.getProjectile().setFireTicks(10000);
        cm.addCooldown("infernal", player.getUniqueId());
    }
}
