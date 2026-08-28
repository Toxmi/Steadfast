package com.toxmi.steadfast.modules.claims;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.managers.DatabaseManager;
import com.toxmi.steadfast.core.utils.SQL;
import com.toxmi.steadfast.core.utils.Scheduler;
import com.toxmi.steadfast.core.utils.TimeFormatter;
import com.toxmi.steadfast.modules.claims.enums.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
    private final Set<ChunkPos> CHUNKS = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> INVITES = new ConcurrentHashMap<>();
    private final Map<Integer, Artifact> artifacts = new ConcurrentHashMap<>();

    private final UUID claimID;
    private final Map<PowerSource, Integer> powerSources = new ConcurrentHashMap<>();
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
        initClaim(claimID, claimName, claimChestLocation);
        addChunk(ChunkPos.of(claimChestLocation));
    }

    public void initClaim(UUID claimID, String claimName, Location claimChestLoc) {
        sch.async(() -> {
            String locKey = String.join(":", String.valueOf(claimChestLoc.getBlockX()), String.valueOf(claimChestLoc.getBlockY()), String.valueOf(claimChestLoc.getBlockZ()));
            db.executeUpdate(SQL.INSERT_CLAIM, claimID.toString(), claimName, locKey);
        });
    }

    public void deleteClaim() {
        sch.async(() -> {
            db.executeUpdate(SQL.DELETE_CLAIM, this.toString());
            for (UUID member : MEMBERS.keySet()) {
                db.executeUpdate(SQL.UPDATE_PLAYER_ROLE, null, member.toString());
            }
        });
    }

    public void loadClaim() {
        sch.async(() -> {
            try (ResultSet rs = db.executeResultStatement(SQL.GET_CLAIM, this.toString())) {
                if (rs == null || !rs.next()) return;
                this.claimName = rs.getString("claimname");
                this.power = rs.getInt("claimpower");
                this.powerSources.put(PowerSource.SPAWNER, rs.getInt("spawnerpower"));
                this.powerSources.put(PowerSource.ARTIFACT, rs.getInt("artifactpower"));
                this.powerSources.put(PowerSource.WEALTH, rs.getInt("wealthpower"));
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
            try (ResultSet rs = db.executeResultStatement(SQL.GET_CLAIM_CHUNKS, this.toString())) {
                ClaimManager claimManager = ClaimManager.get();
                while (rs.next()) {
                    ChunkPos pos = ChunkPos.fromKey(rs.getString("chunkkey"));
                    this.CHUNKS.add(pos);
                    claimManager.indexChunk(pos, this);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load claim chunks", e);
            }
            try (ResultSet rs = db.executeResultStatement(SQL.GET_CLAIM_MEMBERS, this.toString())) {
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

    public void saveClaim() {
        sch.async(() -> {
            db.executeUpdate(SQL.SET_SHIELD_CHARGE, shieldCharge, this.toString());
        });
    }

    public boolean ownsChunk(Chunk chunk) {
        return CHUNKS.contains(ChunkPos.of(chunk));
    }

    public boolean ownsChunk(Location location) {
        return CHUNKS.contains(ChunkPos.of(location));
    }

    public boolean ownsChunk(ChunkPos pos) {
        return CHUNKS.contains(pos);
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

    public boolean damageClaimChest(double damage) {
        claimChestHealth -= damage;
        return claimChestHealth <= 0;
    }

    public boolean isClaimChest(Block block) {
        return block.getLocation().equals(claimChestLoc);
    }

    public boolean isMember(UUID player) {
        return MEMBERS.containsKey(player);
    }

    public boolean isMember(Player player) {
        return MEMBERS.containsKey(player.getUniqueId());
    }

    public void addMember(UUID player) {
        MEMBERS.put(player, ClaimRole.MEMBER);
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_CLAIM, player.toString(), this.toString());
            db.executeUpdate(SQL.UPDATE_PLAYER_ROLE, ClaimRole.MEMBER.name(), player.toString(), this.toString());
        });
    }

    public void removeMember(UUID player) {
        MEMBERS.remove(player);
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_CLAIM, null, player.toString());
            db.executeUpdate(SQL.UPDATE_PLAYER_ROLE, null, player.toString());
        });
    }

    public void removeMember(Player player) {
        MEMBERS.remove(player.getUniqueId());
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_CLAIM, null, player.getUniqueId().toString());
            db.executeUpdate(SQL.UPDATE_PLAYER_ROLE, null, player.getUniqueId().toString());
        });
    }

    public void changeRole(UUID player, ClaimRole role) {
        MEMBERS.put(player, role);
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_ROLE, role.name(), player.toString());
        });
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
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_CLAIM_NAME, claimName, this.toString());
        });
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

    public String getShieldTimeFormatted() {
        return TimeFormatter.getFormattedTime(shieldCharge);
    }

    public void setShieldCharge(int shieldCharge) {
        this.shieldCharge = shieldCharge;
    }

    public ShieldState getShieldState() {
        return shieldState;
    }

    public void setShieldState(ShieldState shieldState) {
        this.shieldState = shieldState;
        sch.async(() -> {
            db.executeUpdate(SQL.CHANGE_CLAIM_SHIELD_STATE, shieldState.name(), this.toString());
        });
    }

    public ShieldMode getShieldMode() {
        return shieldMode;
    }

    public void setShieldMode(ShieldMode shieldMode) {
        this.shieldMode = shieldMode;
        sch.async(() -> {
            db.executeUpdate(SQL.CHANGE_CLAIM_SHIELD_MODE, shieldMode.name(), this.toString());
        });
    }

    public Map<Integer, Artifact> getArtifacts() {
        return artifacts;
    }

    public void addArtifact(Artifact artifact, int slot) {
        artifacts.put(slot, artifact);
        sch.async(() -> {
            db.executeUpdate(SQL.SET_ARTIFACT, slot, artifact.name(), this.toString());
        });
    }

    public void removeArtifact(int slot) {
        artifacts.replace(slot, Artifact.EMPTY);
        sch.async(() -> {
            db.executeUpdate(SQL.SET_ARTIFACT, slot, Artifact.EMPTY.getName(), this.toString());
        });
    }

    public void unlockArtifactSlot(int slot) {
        artifacts.put(slot, Artifact.EMPTY);
        sch.async(() -> {
            db.executeUpdate(SQL.SET_ARTIFACT, slot, Artifact.EMPTY.getName(), this.toString());
        });
    }

    public int getPowerFromASource(PowerSource source) {
        return powerSources.getOrDefault(source, 0);
    }

    public void addPowerFromASource(PowerSource source, int amount) {
        int newAmount = getPowerFromASource(source) + amount;
        powerSources.put(source, newAmount);
        sch.async(() -> {
            db.executeUpdate(SQL.SET_POWER_FROM_SOURCE, source.name(), newAmount, this.toString());
        });
    }

    public void setTotalPower() {
        int totalPower = 0;
        for (int power : powerSources.values()) {
            totalPower += power;
        }
        this.power = totalPower;
        sch.async(() -> {
            db.executeUpdate(SQL.SET_TOTAL_POWER, power, this.toString());
        });
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

    public boolean isMainChunk(Chunk chunk) {
        return ChunkPos.of(claimChestLoc).equals(ChunkPos.of(chunk));
    }

    public void addChunk(Chunk chunk) {
        addChunk(ChunkPos.of(chunk));
    }

    public void addChunk(ChunkPos pos) {
        CHUNKS.add(pos);
        ClaimManager.get().indexChunk(pos, this);
        sch.async(() -> {
            db.executeUpdate(SQL.INSERT_CLAIM_CHUNK, pos.toKey(), this.toString());
        });
    }

    public void removeChunk(ChunkPos pos) {
        CHUNKS.remove(pos);
        ClaimManager.get().unindexChunk(pos);
        sch.async(() -> {
            db.executeUpdate(SQL.DELETE_CLAIM_CHUNK, pos.toKey(), this.toString());
        });
    }

    public void removeChunk(Chunk chunk) {
        removeChunk(ChunkPos.of(chunk));
    }

    @Override
    public String toString() {
        return claimID.toString();
    }
}
