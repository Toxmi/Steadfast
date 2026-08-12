package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Quarry extends CustomEnchant {

    private final List<Material> blockedBlocks = List.of(
            Material.OBSIDIAN,
            Material.CRYING_OBSIDIAN,
            Material.END_PORTAL_FRAME,
            Material.END_GATEWAY,
            Material.BEDROCK,
            Material.BARRIER,
            Material.STRUCTURE_BLOCK,
            Material.STRUCTURE_VOID,
            Material.END_PORTAL,
            Material.NETHER_PORTAL,
            Material.VAULT,
            Material.SPAWNER,
            Material.TRIAL_SPAWNER
    );


    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (!(event instanceof BlockBreakEvent e)) return;
        Location loc = e.getBlock().getLocation();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Block center = loc.getBlock();
        // Loop through all surrounding blocks
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        assert player != null;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block target = center.getRelative(x, y, z);
                    if (target.getType().isAir()) continue;
                    if (blockedBlocks.contains(target.getType()) && center.getType() != target.getType()) continue;
                    if (target.getType().isSolid()) {
                        target.breakNaturally(item);
                        item.damage(1, player);
                    }
                }
            }
        }
    }
}
