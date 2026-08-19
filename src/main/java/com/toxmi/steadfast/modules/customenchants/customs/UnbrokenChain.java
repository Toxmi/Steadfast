package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UnbrokenChain extends CustomEnchant {
    private final Map<ChainStack, Integer> chainStacks = new ConcurrentHashMap<>();

    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        // Check if the attacker has U-Chain stacks on the victim and if not, set stacks to 0
        ChainStack stack = new ChainStack(player, victim);
        int stacks = chainStacks.getOrDefault(stack, 0);

        // Add 1 to the stacks of the attacker
        chainStacks.put(stack, stacks + 1);
        if (stacks >= 2) {
            // Increase damage by 1 + x * stacks
            double multiplier = Math.min(1 + stacks * 0.05, 1.5);
            e.setDamage(e.getDamage() * multiplier);
            victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_CHAIN_BREAK, 1.0f, 1.0f);
        }
    }

    public void removeChain(Player victim) {
        for (ChainStack stack : chainStacks.keySet()) {
            if (stack.victim().equals(victim)) {
                chainStacks.remove(stack);
            }
        }
    }

    private record ChainStack(Player attacker, Player victim) {
    }
}
