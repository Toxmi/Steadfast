package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class Ascent extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!e.getDamageSource().getDamageType().equals(DamageType.MACE_SMASH)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        assert player != null;
        if (!player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.WIND_BURST)) return;
        victim.setVelocity(victim.getVelocity().add(new Vector(0, cm.getVar1("ascent"), 0)));
    }
}
