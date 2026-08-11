package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Berserk extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        assert player != null;
        if (cm.isOnCooldown("berserk", player.getUniqueId())) return;
        // Add Strength 3 to player for X duration
        addPotionEffect(PotionEffectType.STRENGTH, player, cm.getVar1("berserk"), 3);

        // Add Strength 2 to player for X duration after previous strength runs out
        plugin.getServer().getRegionScheduler().runDelayed(plugin, player.getLocation(), task -> {
            addPotionEffect(PotionEffectType.STRENGTH, player, cm.getVar2("berserk"), 2);
        }, (long) cm.getVar1("berserk") * 20);
    }
}
