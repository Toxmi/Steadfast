package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.core.utils.Potion.addPotionEffect;
import static com.toxmi.steadfast.core.utils.Potion.removePotionEffect;

public class Ninja extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (event != null) return;
        addPotionEffect(PotionEffectType.INVISIBILITY, player, 2, 1);
        removePotionEffect(PotionEffectType.GLOWING, player);
    }
}
