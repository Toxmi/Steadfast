package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Clapback extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        if (cm.isOnCooldown("clapback", player.getUniqueId())) return;
        if (!(e.getDamageSource().getCausingEntity() instanceof Player attacker)) return;
        // Damage the attacker by the same amount as the damage dealt to the victim
        attacker.damage(e.getDamage());
        // Add Slowness effect to the attacker for 5 seconds
        addPotionEffect(PotionEffectType.SLOWNESS, attacker, 5, 4);
        cm.addCooldown("clapback", player.getUniqueId());
    }
}
