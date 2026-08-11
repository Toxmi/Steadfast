package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trickster extends CustomEnchant {
    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        if (Math.random() < cm.getVar1("trickster")) return;

        // Get all items in the victims hotbar
        List<ItemStack> hotbar = new ArrayList<>();
        for (int i = 0; i < 9; i++) {

            hotbar.add(victim.getInventory().getItem(i));
        }

        // Shuffle the hotbar
        Collections.shuffle(hotbar);

        // Set the hotbar to the randomized version
        int slot = 0;
        for (ItemStack item : hotbar) {
            if (item == null) {
                victim.getInventory().setItem(slot, new ItemStack(Material.AIR));
                slot++;
                continue;
            }
            victim.getInventory().setItem(slot, item);
            slot++;
        }
        victim.updateInventory();


    }
}
