package com.toxmi.steadfast.modules.claims.listeners;

import com.toxmi.steadfast.modules.claims.Claim;
import com.toxmi.steadfast.modules.claims.ClaimManager;
import com.toxmi.steadfast.modules.claims.enums.ShieldState;
import io.papermc.paper.event.block.TargetHitEvent;
import io.papermc.paper.event.player.PlayerBedFailEnterEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ClaimProtectionListener implements Listener {
    private final ClaimManager claimManager;

    public ClaimProtectionListener() {
        this.claimManager = ClaimManager.get();
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Claim claim = claimManager.getClaim(block.getLocation());
        if (claim == null) return;
        Player player = event.getPlayer();
        if (claim.isClaimChest(block)) {
            event.setCancelled(true);
            return;
        }
        if (!claim.isMember(player)) {
            event.setCancelled(true);
            return;
        }

        if (claim.isInCombat() && block.getType().equals(Material.SPAWNER)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You cannot break spawners in combat!"));
            return;
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Claim claim = claimManager.getClaim(block.getLocation());
        if (claim == null) return;
        if (claim.getShieldState().equals(ShieldState.ACTIVATING)) return;
        Player player = event.getPlayer();
        if (!claimManager.isAllowedToUse(player, block.getLocation())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onChorusTeleport(PlayerTeleportEvent event) {
        if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.CONSUMABLE_EFFECT)) return;
        Claim claim = claimManager.getClaim(event.getTo());
        if (claim == null) return;

        if (!claimManager.isAllowedToUse(event.getPlayer(), event.getTo())) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        Claim claim = claimManager.getClaim(block.getLocation());
        if (claim == null) return;
        Player player = event.getPlayer();
        if (!claimManager.isAllowedToUse(player, block.getLocation())) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.ALLOW);
            return;
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Wither)
                && !(entity instanceof Ravager)
                && !(entity instanceof Silverfish)
                && !(entity instanceof Enderman)) return;

        Block block = event.getBlock();
        Claim claim = claimManager.getClaim(block.getChunk());
        if (claim != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(TargetHitEvent event) {
        Block block = event.getHitBlock();
        Player player = (Player) event.getEntity().getShooter();
        if(player == null || block == null) return;
        Claim claim = claimManager.getClaim(block.getChunk());
        if (claim == null || claim.isMember(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Claim claim = claimManager.getClaim(block.getChunk());
            return claim != null;
        });
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();
        Claim fromClaim = claimManager.getClaim(from.getChunk());
        Claim toClaim = claimManager.getClaim(to.getChunk());
        if (fromClaim == null && toClaim != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();
        Claim cd = claimManager.getClaim(block.getChunk());
        if (cd == null) return;
        if (event.getEntity() instanceof Player) return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Claim claim = claimManager.getClaim(block.getChunk());
            return claim != null;
        });
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Block piston = event.getBlock();
        Claim claim = claimManager.getClaim(piston.getChunk());
        if (claim != null) return;
        for (Block moved : event.getBlocks()) {
            Block target = moved.getRelative(event.getDirection());
            Claim targetClaim = claimManager.getClaim(target.getChunk());
            if (targetClaim != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) return;
        Block piston = event.getBlock();
        Claim claim = claimManager.getClaim(piston.getChunk());
        if (claim != null) return;
        for (Block moved : event.getBlocks()) {
            Claim fromClaim = claimManager.getClaim(moved.getChunk());
            if (fromClaim != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (claimManager.getClaim(event.getBed().getChunk()) == null) return;
        Player player = event.getPlayer();
        if(claimManager.isAllowedToUse(player, event.getBed().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBedFail(PlayerBedFailEnterEvent event) {
        if (claimManager.getClaim(event.getBed().getChunk()) == null) return;
        Player player = event.getPlayer();
        if(claimManager.isAllowedToUse(player, event.getBed().getLocation())) return;
        event.setCancelled(true);
    }


    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Claim cd = claimManager.getClaim(block.getChunk());

        if (claimManager.isAllowedToUse(player, block.getLocation())) return;
        if (cd.getShieldState() == ShieldState.ACTIVATING) return;
        event.setCancelled(true);
    }
}
