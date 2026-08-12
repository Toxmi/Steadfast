package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

public class Concuss extends CustomEnchant {

    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        if (!e.getDamageSource().getDamageType().equals(DamageType.MACE_SMASH)) return;
        assert player != null;
        if (cm.isOnCooldown("concuss", player.getUniqueId())) return;
        playerScheduler(victim, () -> {
            cl.disableCustoms(victim.getUniqueId());
            playerScheduler(victim, () -> {
                cl.enableCustoms(victim.getUniqueId());
            }, (long) cm.getVar1("concuss") * 20);
        }, (long) cm.getVar2("concuss") * 20);
        cm.addCooldown("concuss", player.getUniqueId());
    }
}
