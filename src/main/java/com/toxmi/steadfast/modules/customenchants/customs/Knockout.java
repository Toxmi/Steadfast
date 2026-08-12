package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class Knockout extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof PlayerShieldDisableEvent e)) return;
        if (!(e.getDamager() instanceof Player attacker)) return;
        e.setCooldown((int) (20 * cm.getVar1("knockout")));
    }
}
