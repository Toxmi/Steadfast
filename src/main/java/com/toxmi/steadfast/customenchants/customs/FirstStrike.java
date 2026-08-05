package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirstStrike extends CustomEnchant {
    private final Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void useAbility(Player player, Event event) {
        // TO DO
        if (!(event instanceof EntityDamageEvent e)) return;
        if (cm.isOnCooldown("firststrike", player.getUniqueId())) return;
        Entity victim = e.getEntity();
        if (cooldown.containsKey(victim.getUniqueId())) {
            long time = cooldown.get(victim.getUniqueId());
            if (time + cm.getVar2("firststrike") * 1000 < System.currentTimeMillis()) return;

        }
        e.setDamage(e.getDamage() * cm.getVar1("firststrike"));
        cooldown.put(victim.getUniqueId(), System.currentTimeMillis());
        cm.addCooldown("firststrike", player.getUniqueId());
    }
}
