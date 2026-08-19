package com.toxmi.steadfast.modules.claims;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.managers.DatabaseManager;
import com.toxmi.steadfast.core.utils.SQL;
import com.toxmi.steadfast.core.utils.Scheduler;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Claim {
    private final Steadfast plugin;
    private final Scheduler sch;
    private final DatabaseManager db;

    private final Map<UUID, ClaimRole> MEMBERS = new ConcurrentHashMap<>();
    private final Set<String> CHUNKS = Collections.synchronizedSet(new HashSet<>());
    private final Map<UUID, Long> INVITES = new ConcurrentHashMap<>();
    private final Map<Integer, Artifact> artifacts = new ConcurrentHashMap<>();

    private final UUID claimID;
    private final Map<String, Integer> powerSources = new ConcurrentHashMap<>();
    private String claimName;
    private Location claimChestLoc;
    private boolean inCombat = false;
    private long combatDuration = 0L;
    private double claimChestHealth = 100;
    private double claimChestMaxHealth = 100;
    private int power = 0;
    private ShieldState shieldState = ShieldState.INACTIVE;
    private int shieldCharge = 0;
    private ShieldMode shieldMode = ShieldMode.AUTOMATIC;

    public Claim(UUID claimID) {
        this.plugin = Steadfast.get();
        this.sch = Scheduler.get();
        this.db = DatabaseManager.get();
        this.claimID = claimID;
        loadClaim();
    }

    public Claim(Player player, Location claimChestLocation) {
        this.plugin = Steadfast.get();
        this.sch = Scheduler.get();
        this.db = DatabaseManager.get();
        this.claimID = UUID.randomUUID();
        this.claimName = String.format("%s's Claim", player.getName());
        this.claimChestLoc = claimChestLocation;
        CHUNKS.add(getChunkKey(claimChestLocation));
    }

    public void loadClaim() {
        sch.async(() -> {
            try (ResultSet rs = db.executeResultStatement(SQL.GET_CLAIM, claimID.toString())) {
                if (rs == null || !rs.next()) return;
                this.claimName = rs.getString("claimname");
                this.power = rs.getInt("claimpower");
                this.powerSources.put("spawners", rs.getInt("spawnerpower"));
                this.powerSources.put("artifacts", rs.getInt("artifactpower"));
                this.powerSources.put("wealth", rs.getInt("wealthpower"));
                String[] claimChestLocString = rs.getString("claimchestloc").split(":");
                double x = Double.parseDouble(claimChestLocString[0]);
                double y = Double.parseDouble(claimChestLocString[1]);
                double z = Double.parseDouble(claimChestLocString[2]);
                World world = plugin.getServer().getWorld(claimChestLocString[3]);
                this.claimChestLoc = new Location(world, x, y, z);
                this.shieldCharge = rs.getInt("shieldcharge");
                this.shieldMode = ShieldMode.valueOf(rs.getString("shieldmode"));
                this.shieldState = ShieldState.valueOf(rs.getString("shieldstate"));
                this.artifacts.put(1, Artifact.valueOf(rs.getString("artifact1")));
                this.artifacts.put(2, Artifact.valueOf(rs.getString("artifact2")));
                this.artifacts.put(3, Artifact.valueOf(rs.getString("artifact3")));

            } catch (SQLException e) {
                throw new RuntimeException("Failed to load claim", e);
            }
            try (ResultSet rs = db.executeResultStatement(SQL.GET_CLAIM_CHUNKS, claimID)) {
                while (rs.next()) {
                    String chunkKey = rs.getString("chunkkey");
                    this.CHUNKS.add(chunkKey);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load claim chunks", e);
            }
            try (ResultSet rs = db.executeResultStatement(SQL.GET_CLAIM_MEMBERS, claimID)) {
                while (rs.next()) {
                    UUID memberUUID = UUID.fromString(rs.getString("playerid"));
                    ClaimRole role = ClaimRole.valueOf("claimrole");
                    MEMBERS.put(memberUUID, role);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load claim members from database.", e);
            }
        });
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

    public Map<UUID, ClaimRole> getMembersMap() {
        return MEMBERS;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void addInvite(UUID target) {
        INVITES.put(target, System.currentTimeMillis());
    }

    public void removeInvite(UUID target) {
        INVITES.remove(target);
    }


    public String getClaimName() {
        return claimName;
    }

    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public Location getClaimChestLoc() {
        return claimChestLoc;
    }

    public UUID getClaimID() {
        return claimID;
    }


    public int getShieldCharge() {
        return shieldCharge;
    }

    public void setShieldCharge(int shieldCharge) {
        this.shieldCharge = shieldCharge;
    }

    public ShieldState getShieldState() {
        return shieldState;
    }

    public void setShieldState(ShieldState shieldState) {
        this.shieldState = shieldState;
    }

    public ShieldMode getShieldMode() {
        return shieldMode;
    }

    public void setShieldMode(ShieldMode shieldMode) {
        this.shieldMode = shieldMode;
    }

    public Map<Integer, Artifact> getArtifacts() {
        return artifacts;
    }

    public void addArtifact(Artifact artifact) {
        artifacts.put(artifacts.size() + 1, artifact);
    }

    public void removeArtifact(int slot) {
        artifacts.replace(slot, null);
    }

    public void unlockArtifactSlot(int slot) {
        artifacts.put(slot, null);
    }

    public int getPowerFromASource(String source) {
        return powerSources.getOrDefault(source.toLowerCase(), 0);
    }

    public void addPowerFromASource(String source, int amount) {
        powerSources.put(source.toLowerCase(), getPowerFromASource(source) + amount);
    }

    public int getChunkCount() {
        return CHUNKS.size();
    }

    public UUID getOwner() {
        return MEMBERS.entrySet().stream().filter(e -> e.getValue() == ClaimRole.OWNER).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public Map<UUID, Long> getInvites() {
        return INVITES;
    }
}
