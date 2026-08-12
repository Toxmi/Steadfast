package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Hardhat extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        if (e.getDamageSource().getDamageType().equals(DamageType.MACE_SMASH) || e.getDamageSource().getDamageType().equals(DamageType.FALLING_STALACTITE)) {
            e.setDamage(e.getDamage() * (1 - cm.getVar1("hardhat")));
        }
    }
}
