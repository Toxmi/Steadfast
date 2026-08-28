package com.toxmi.steadfast.modules.claims.listeners;

import com.toxmi.steadfast.core.utils.Keys;
import com.toxmi.steadfast.modules.claims.Claim;
import com.toxmi.steadfast.modules.claims.ClaimManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.toxmi.steadfast.core.utils.HologramBuilder.createHolo;
import static com.toxmi.steadfast.core.utils.Str.cc;
import static com.toxmi.steadfast.core.utils.Str.cm;

public class ClaimChestListener implements Listener {

    private final ClaimManager claimManager;
    private final List<String> allowedWorlds = new ArrayList<>();
    public ClaimChestListener() {
        this.claimManager = ClaimManager.get();
        reload();
    }

    public void reload() {
        allowedWorlds.clear();
        allowedWorlds.addAll(claimManager.getConfig().getStringList("allowed-worlds"));
    }


    @EventHandler
    public void onClaimChestPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Block block = event.getBlock();
        if (!Keys.hasKey(item, Keys.itemKey)) return;
        if (!Keys.getKey(item, Keys.itemKey).equalsIgnoreCase("claimChest")) return;

        // Check if the player can make claims in this dimension
        if (allowedWorlds.contains(player.getWorld().getName())) {
            player.sendMessage(cm("<Gray>▪ <Red>You cannot place a Claim in this dimension."));
            event.setCancelled(true);
            return;
        }

        // Check if the player is already in a claim
        if (claimManager.getClaim(player) != null) {
            player.sendMessage(cm("<Gray>▪ <Red>You are already in a Claim team."));
            event.setCancelled(true);
            return;
        }

        // Check if the position is within x chunks of an enemy claim
        if (claimManager.isTooCloseToEnemyClaim(block.getLocation(),null)) {
            player.sendMessage(cm("<Gray>▪ <Red>You cannot place a Claim chest too close to an enemy Claim."));
            event.setCancelled(true);
            return;
        }

        // Make sure there is an air block above the chest
        if (!block.getRelative(0,1,0).getType().equals(Material.AIR)) {
            player.sendMessage(cm("<Gray>▪ <Red>Clear the area above the Claim chest."));
            event.setCancelled(true);
            return;
        }

        // Create the Claim if all previous checks passed
        Claim claim = claimManager.createClaim(player, block.getLocation());
        createClaimHolo(claim, block.getLocation());
    }

    private void createClaimHolo(Claim claim, Location location) {
        createHolo(location.add(0,0.5,0),Keys.claimHoloKey, claim.getClaimID().toString(),
                List.of(
                        cc(claim.getClaimName()).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD,true),
                        cc(""),
                        cm("<White>Shield Charge: <Yellow><time>",
                                Placeholder.component("time", cc(claim.getShieldTimeFormatted()))
                                )
                )
        );
    }

}
