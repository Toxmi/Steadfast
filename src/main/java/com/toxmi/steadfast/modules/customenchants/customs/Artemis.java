package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.core.utils.Keys;
import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.core.utils.Potion.addPotionEffect;

public class Artemis extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (event instanceof EntityShootBowEvent e) {
            e.getProjectile().getPersistentDataContainer().set(Keys.arrowForceKey, PersistentDataType.FLOAT, e.getForce());
        } else if (event instanceof ProjectileHitEvent e) {
            if (!(e.getEntity() instanceof Arrow arrow)) return;
            if (!(arrow.getShooter() instanceof Player attacker)) return;
            if (e.getHitEntity() == null) return;
            float force = arrow.getPersistentDataContainer().getOrDefault(Keys.arrowForceKey, PersistentDataType.FLOAT,0f);
            if (force < 1.0f) return;
            addPotionEffect(PotionEffectType.SPEED, attacker, cm.getVar1("artemis"), 2);
        }

    }
}
