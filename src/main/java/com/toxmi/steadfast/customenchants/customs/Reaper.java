package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Reaper extends CustomEnchant {
    private final List<PotionEffectType> positiveEffects = List.of(
            PotionEffectType.HASTE,
            PotionEffectType.SPEED,
            PotionEffectType.REGENERATION,
            PotionEffectType.RESISTANCE,
            PotionEffectType.ABSORPTION
    );

    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        if (Math.random() > cm.getVar1("reaper")) return;

        // Loop through all active potion effects of the victim
        victim.getActivePotionEffects().forEach(effect  -> {
            // Check if the potion effect is a positive effect
            if (positiveEffects.contains(effect.getType())) {
                if (effect.getDuration() > 10 * 20) {
                    // Remove the potion effect and add it back with 10 seconds shorter duration
                    victim.removePotionEffect(effect.getType());
                    addPotionEffect(effect.getType(), victim, effect.getDuration() / 10- 10, effect.getAmplifier());
                } else {
                    victim.removePotionEffect(effect.getType());
                }
            }
        });
    }
}
