package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class Knockout extends CustomEnchant {
    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof PlayerShieldDisableEvent e)) return;
        if (!(e.getDamager() instanceof Player attacker)) return;
        attacker.setCooldown(Material.SHIELD, (int) cm.getVar1("knockout") * 20);
    }
}
