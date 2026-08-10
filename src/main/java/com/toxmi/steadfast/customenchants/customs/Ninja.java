package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;
import static com.toxmi.steadfast.utils.Potion.removePotionEffect;

public class Ninja extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (event != null) return;
        addPotionEffect(PotionEffectType.INVISIBILITY, player, 2, 1);
        removePotionEffect(PotionEffectType.GLOWING, player);
    }
}
