package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.Nullable;

public class Bolt extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof ProjectileHitEvent e)) return;
        if (!(e.getEntity() instanceof Trident trident) || !(trident.getShooter() instanceof Player shooter)) return;
        if (!(e.getHitEntity() instanceof LivingEntity le)) return;
        if (cm.isOnCooldown("bolt", shooter.getUniqueId())) return;
        le.damage(cm.getVar1("bolt"));
        le.getWorld().strikeLightning(le.getLocation());
        cm.addCooldown("bolt", shooter.getUniqueId());

    }
}
