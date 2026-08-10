package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Adrenaline extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityDeathEvent)) return;
        addPotionEffect(PotionEffectType.STRENGTH, player, cm.getVar1("adrenaline"), 2);
        addPotionEffect(PotionEffectType.SPEED, player, cm.getVar2("adrenaline"), 2);
        addPotionEffect(PotionEffectType.REGENERATION, player, cm.getVar3("adrenaline"), 1);
    }

}
