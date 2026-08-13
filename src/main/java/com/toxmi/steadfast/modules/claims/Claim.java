package com.toxmi.steadfast.modules.claims;

import com.toxmi.steadfast.Steadfast;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class Claim {
    private final Steadfast plugin;

    private final Map<UUID, ClaimRole> MEMBERS = new HashMap<>();
    private final Set<String> CHUNKS = new HashSet<>();
    private final Map<UUID, Long> INVITES = new HashMap<>();

    private UUID claimID;
    private String claimName;
    private Location claimChestLoc;

    private boolean inCombat = false;
    private long combatDuration = 0L;

    private double claimChestHealth = 100;
    private double claimChestMaxHealth = 100;

    private double power = 0.0;


    public Claim(UUID claimID) {
        this.plugin = Steadfast.get();
    }

    public Claim(Player player, Location claimChestLocation) {
        this.plugin = Steadfast.get();
        this.claimID = UUID.randomUUID();
        this.claimName = String.format("%s's Claim", player.getName());
        this.claimChestLoc = claimChestLocation;
        CHUNKS.add(getChunkKey(claimChestLocation));
    }

    public void loadClaim() {

    }

    public String getChunkKey(Chunk chunk) {
        return String.format("%d:%d:%s", chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }

    public String getChunkKey(Location location) {
        Chunk chunk = location.getChunk();
        return String.format("%d:%d:%s", chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }

    public Chunk getChunk(String chunkKey) {
        int x = Integer.parseInt(chunkKey.split(":")[0]);
        int z = Integer.parseInt(chunkKey.split(":")[1]);
        World world = plugin.getServer().getWorld(chunkKey.split(":")[2]);
        return world.getChunkAt(x, z);
    }

    public boolean ownsChunk(Chunk chunk) {
        return CHUNKS.contains(getChunkKey(chunk));
    }

    public boolean ownsChunk(Location location) {
        return CHUNKS.contains(getChunkKey(location));
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }

    public long getCombatDuration() {
        return combatDuration;
    }

    public void setCombatDuration(long combatDuration) {
        this.combatDuration = combatDuration;
    }

    public double getClaimChestHealth() {
        return claimChestHealth;
    }

    public void setClaimChestHealth(double claimChestHealth) {
        this.claimChestHealth = claimChestHealth;
    }

    public double getClaimChestMaxHealth() {
        return claimChestMaxHealth;
    }

    public void setClaimChestMaxHealth(double claimChestMaxHealth) {
        this.claimChestMaxHealth = claimChestMaxHealth;
    }

    public boolean isMember(UUID player) {
        return MEMBERS.containsKey(player);
    }

    public boolean isMember(Player player) {
        return MEMBERS.containsKey(player.getUniqueId());
    }

    public void addMember(UUID player) {
        MEMBERS.put(player, ClaimRole.MEMBER);
    }

    public void removeMember(UUID player) {
        MEMBERS.remove(player);
    }

    public void removeMember(Player player) {
        MEMBERS.remove(player.getUniqueId());
    }

    public void changeRole(UUID player, ClaimRole role) {
        MEMBERS.put(player, role);
    }

    public Set<UUID> getMembers() {
        return MEMBERS.keySet();
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void addInvite(UUID target) {
        INVITES.put(target, System.currentTimeMillis());
    }

    public void removeInvite(UUID target) {
        INVITES.remove(target);
    }


}
