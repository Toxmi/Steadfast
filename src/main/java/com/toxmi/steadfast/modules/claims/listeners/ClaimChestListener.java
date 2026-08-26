package com.toxmi.steadfast.modules.claims.listeners;

import com.toxmi.steadfast.core.utils.Keys;
import com.toxmi.steadfast.modules.claims.ClaimManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ClaimChestListener implements Listener {

    private final ClaimManager claimManager;

    public ClaimChestListener() {
        this.claimManager = ClaimManager.get();
    }


    @EventHandler
    public void onClaimChestPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Block block = event.getBlock();
        if (!Keys.hasKey(item, Keys.itemKey)) return;
        if (!Keys.getKey(item, Keys.itemKey).equalsIgnoreCase("claimChest")) return;
        if (claimManager.getClaim(player) != null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<Gray>▪ <Red>You are already in a Claim team."));
            event.setCancelled(true);
            return;
        }
    }

}
