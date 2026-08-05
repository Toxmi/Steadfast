package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;

public class UnbrokenChain extends CustomEnchant {
    private final Map<ChainStack, Integer> chainStacks = new HashMap<>();

    private record ChainStack(Player attacker, Player victim) {
    }

    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        ChainStack stack = new ChainStack(player, victim);
        int stacks = chainStacks.getOrDefault(stack, 0);
        chainStacks.put(stack, stacks + 1);
        if (stacks >= 2) {
            double multiplier = Math.min(1 + stacks * 0.05, 1.5);
            e.setDamage(e.getDamage() * multiplier);
        }
    }

    public void removeChain(Player victim) {
        for (ChainStack stack : chainStacks.keySet()) {
            if (stack.victim().equals(victim)) {
                chainStacks.remove(stack);
            }
        }
    }
}
