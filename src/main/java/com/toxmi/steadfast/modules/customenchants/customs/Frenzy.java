package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.core.utils.Potion.addPotionEffect;

public class Frenzy extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (event != null) return;
//        if (!getPDC(player.getInventory().getHelmet(), Keys.customKey).equalsIgnoreCase("extinguish")) return;
        addPotionEffect(PotionEffectType.HASTE, player, 1, (int) cm.getVar1("frenzy"));
    }
}
