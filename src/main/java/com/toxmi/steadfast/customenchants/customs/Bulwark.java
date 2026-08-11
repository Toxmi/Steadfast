package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Bulwark extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof PlayerShieldDisableEvent e)) return;
        assert player != null;
        if (cm.isOnCooldown("bulwark", player.getUniqueId())) return;
        addPotionEffect(PotionEffectType.RESISTANCE, player, cm.getVar1("bulwark"), 2);
        cm.addCooldown("bulwark", player.getUniqueId());
    }
}
