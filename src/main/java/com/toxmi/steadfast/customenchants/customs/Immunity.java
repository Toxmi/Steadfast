package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import com.toxmi.steadfast.utils.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.removePotionEffect;

public class Immunity extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (event != null) return;
//        if (!getPDC(player.getInventory().getBoots(), Keys.customKey).equalsIgnoreCase("immunity")) return;
        removePotionEffect(PotionEffectType.WEAKNESS, player);
        removePotionEffect(PotionEffectType.WITHER, player);
        removePotionEffect(PotionEffectType.POISON, player);
        removePotionEffect(PotionEffectType.SLOW_FALLING, player);

    }
}
