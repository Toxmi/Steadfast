package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Lifesteal extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!(e.getEntity() instanceof Player)) return;
        if (Math.random() > cm.getVar1("lifesteal")) return;
        addPotionEffect(PotionEffectType.REGENERATION, player, cm.getVar2("lifesteal"), 1);
    }
}
