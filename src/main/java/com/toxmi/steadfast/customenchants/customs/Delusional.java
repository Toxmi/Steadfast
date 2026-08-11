package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Delusional extends CustomEnchant {

    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (event != null) return;
        boolean armorWorn = false;
        // Check if armor is worn
        assert player != null;
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null) {
                armorWorn = true;
                break;
            }
        }
        // Add Strength 3 if armor is not worn
        if (armorWorn) return;
        addPotionEffect(PotionEffectType.STRENGTH, player, 2, 3);
    }
}
