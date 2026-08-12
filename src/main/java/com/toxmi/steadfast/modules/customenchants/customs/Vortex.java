package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.Nullable;

public class Vortex extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof ProjectileHitEvent e)) return;
        if (!(e.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player shooter)) return;
        if (cm.isOnCooldown("vortex", shooter.getUniqueId())) return;
        Location loc = trident.getLocation();
        // Loop through all players in an X block radius and pull them towards the trident
        for (Player p : loc.getNearbyPlayers(cm.getVar1("vortex"))) {
            p.setVelocity(p.getLocation().getDirection().multiply(cm.getVar1("vortex")));
        }
        cm.addCooldown("vortex", shooter.getUniqueId());
    }
}
