package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class Demolotionist extends CustomEnchant {
    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        if (e.getDamageSource().getDamageType().equals(DamageType.EXPLOSION) || e.getDamageSource().getDamageType().equals(DamageType.PLAYER_EXPLOSION)) {
            e.setDamage(e.getDamage() * (1 - cm.getVar1("demolotionist")));
        }
    }
}
