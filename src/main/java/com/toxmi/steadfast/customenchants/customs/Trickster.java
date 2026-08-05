package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trickster extends CustomEnchant {
    @Override
    public void useAbility(Player player, Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (Math.random() > cm.getVar1("trickster")) return;
        List<ItemStack> hotbar = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            hotbar.set(i, player.getInventory().getItem(i));
        }
        Collections.shuffle(hotbar);
        for (ItemStack item : hotbar) {
            if (item == null) player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), item);
        }
        player.updateInventory();


    }
}
