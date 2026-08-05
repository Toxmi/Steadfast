package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class Cleaving extends CustomEnchant {
    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof PlayerShieldDisableEvent e)) return;
        e.getPlayer().damage(cm.getVar1("cleaving"));
    }
}
