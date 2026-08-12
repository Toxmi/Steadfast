package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirstStrike extends CustomEnchant {
    private final Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        assert player != null;
        if (cm.isOnCooldown("firststrike", player.getUniqueId())) return;
        Entity victim = e.getEntity();
        player.sendMessage("First Strike!");
        // Check if the victim has been struck by another first strike in the last X seconds
        if (cooldown.containsKey(victim.getUniqueId())) {
            long time = cooldown.get(victim.getUniqueId());
            if (time + cm.getVar2("firststrike") * 1000 < System.currentTimeMillis()) return;
        }

        // Increase damage by X
        e.setDamage(e.getDamage() * cm.getVar1("firststrike"));

        // Add cooldowns to victim and the user
        cooldown.put(victim.getUniqueId(), System.currentTimeMillis());
        cm.addCooldown("firststrike", player.getUniqueId());
        victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0f, 1.0f);
    }
}
