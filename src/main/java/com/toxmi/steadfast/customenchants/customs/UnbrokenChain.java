package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;

public class UnbrokenChain extends CustomEnchant {
    private final Map<ChainStack, Integer> chainStacks = new HashMap<>();

    private record ChainStack(Player attacker, Player victim) {}

    @Override
    public void useAbility(Player player, Event event) {
        // TO DO
    }

    public void removeChain(Player victim) {
        for (ChainStack stack : chainStacks.keySet()) {
            if (stack.victim().equals(victim)) {
                chainStacks.remove(stack);
            }
        }
    }
}
