package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import com.toxmi.steadfast.utils.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Endurance extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (event != null) return;
//        if (!getPDC(player.getInventory().getLeggings(), Keys.customKey).equalsIgnoreCase("endurance")) return;
        addPotionEffect(PotionEffectType.SPEED, player, 2, (int) cm.getVar1("endurance"));
    }
}
