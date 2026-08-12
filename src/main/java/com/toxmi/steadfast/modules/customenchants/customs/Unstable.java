package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.core.utils.TeleportUtil;
import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class Unstable extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        assert player != null;
        if (cm.isOnCooldown("unstable", player.getUniqueId())) return;
        if (player.getHealth() > Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue() * 0.4) return;

        TeleportUtil.tryRandomTeleport(player,cm.getVar1("unstable"));
        cm.addCooldown("unstable", player.getUniqueId());
    }
}
