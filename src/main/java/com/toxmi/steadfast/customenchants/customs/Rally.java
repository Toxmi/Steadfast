package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import static com.toxmi.steadfast.utils.Potion.*;


public class Rally extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (event != null) return;
        if (player.isBlocking()) {
            addPotionEffect(PotionEffectType.REGENERATION, player, 2, (int) cm.getVar1("rally"));
        }
    }
}
