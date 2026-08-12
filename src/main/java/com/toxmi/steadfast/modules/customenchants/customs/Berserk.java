package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.core.utils.Potion.addPotionEffect;

public class Berserk extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        assert player != null;
        if (cm.isOnCooldown("berserk", player.getUniqueId())) return;
        // Add Strength 3 to player for X duration
        addPotionEffect(PotionEffectType.STRENGTH, player, cm.getVar1("berserk"), 3);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f);
        // Add Strength 2 to player for X duration after previous strength runs out
        plugin.getServer().getRegionScheduler().runDelayed(plugin, player.getLocation(), task -> {
            addPotionEffect(PotionEffectType.STRENGTH, player, cm.getVar2("berserk"), 2);
        }, (long) cm.getVar1("berserk") * 20);
    }
}
