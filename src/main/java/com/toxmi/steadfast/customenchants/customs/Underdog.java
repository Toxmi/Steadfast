package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import com.toxmi.steadfast.utils.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Underdog extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (event != null) return;
        if (!getPDC(player.getInventory().getItemInMainHand(), Keys.customKey).equalsIgnoreCase("underdog")) return;
        if (player.getHealth() < 16) {
            addPotionEffect(PotionEffectType.STRENGTH, player, 2, 3);
        }
    }
}
