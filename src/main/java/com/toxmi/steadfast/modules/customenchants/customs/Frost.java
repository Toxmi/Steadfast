package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.core.utils.Potion.addPotionEffect;

public class Frost extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof ProjectileHitEvent e)) return;
        if (!(e.getEntity() instanceof Arrow arrow) || !(arrow.getShooter() instanceof Player attacker)) return;
        if (cm.isOnCooldown("frost", attacker.getUniqueId())) return;
        if (!(e.getHitEntity() instanceof Player victim)) return;

        addPotionEffect(PotionEffectType.SLOWNESS, victim, cm.getVar1("frost"), 1);
        addPotionEffect(PotionEffectType.MINING_FATIGUE, victim, cm.getVar1("frost"), 1);
        cm.addCooldown("frost", attacker.getUniqueId());

    }
}
