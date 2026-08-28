package com.toxmi.steadfast.modules.claims;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.managers.DatabaseManager;
import com.toxmi.steadfast.core.utils.SQL;
import com.toxmi.steadfast.core.utils.Scheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimManager {
    private static ClaimManager instance;
    private final Steadfast plugin;
    private final Scheduler sch;
    private final DatabaseManager db;

    private final Map<UUID, Claim> claims = new ConcurrentHashMap<>();
    private final Map<ChunkPos, Claim> chunkIndex = new ConcurrentHashMap<>();
    private final Map<UUID, ScheduledTask> claimShieldTasks = new ConcurrentHashMap<>();



    private File file;
    private FileConfiguration config;


    private int CLAIM_DISTANCE;


    public ClaimManager() {
        instance = this;
        this.plugin = Steadfast.get();
        this.sch = Scheduler.get();
        this.db = DatabaseManager.get();
        loadConfig();
        load();
    }

    public synchronized static ClaimManager get() {
        if (instance == null) {
            instance = new ClaimManager();
        }
        return instance;
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "claims.yml");

        if (!file.exists()) {
            plugin.saveResource("claims.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void load() {
        CLAIM_DISTANCE = config.getInt("claim-distance");
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void loadClaims() {
        sch.async(() -> {
            try (ResultSet rs = db.executeResultStatement(SQL.GET_ALL_CLAIM_IDS)) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("claimid"));
                    Claim claim = new Claim(uuid);
                    claims.put(uuid, claim);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load all claims", e);
            }
        });
    }

    public Claim getClaim(UUID uuid) {
        return claims.get(uuid);
    }

    public Claim getClaim(String name) {
        return claims.values().stream().filter(claim -> claim.getClaimName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Claim getClaim(Chunk chunk) {
        return chunkIndex.get(ChunkPos.of(chunk));
    }

    public Claim getClaim(Location location) {
        return chunkIndex.get(ChunkPos.of(location));
    }

    public Claim getClaim(ChunkPos pos) {
        return chunkIndex.get(pos);
    }

    void indexChunk(ChunkPos pos, Claim claim) {
        chunkIndex.put(pos, claim);
    }

    void unindexChunk(ChunkPos pos) {
        chunkIndex.remove(pos);
    }

    void unindexClaim(Claim claim) {
        chunkIndex.values().removeIf(indexed -> indexed == claim);
    }

    public Claim getClaimByPlayer(UUID player) {
        return claims.values().stream().filter(claim -> claim.isMember(player)).findFirst().orElse(null);
    }

    public Claim getClaim(Player player) {
        return getClaimByPlayer(player.getUniqueId());
    }

    public boolean isAllowedToUse(Player player, Location loc) {
        if (player.hasPermission("steadfast.bypassclaim")) return true;
        Claim claim = getClaim(loc);
        return claim == null || claim.isMember(player);
    }

    public int getClaimRank(Claim claim) {
        List<Claim> sorted = claims.values().stream()
                .sorted(Comparator.comparingInt(Claim::getPower).reversed())
                .toList();

        int index = sorted.indexOf(claim);

        return index == -1 ? -1 : index + 1;
    }

    public void deleteClaim(UUID claimID) {
        Claim claim = claims.remove(claimID);
        if (claim == null) return;
        unindexClaim(claim);
        claim.deleteClaim();
    }

    public Claim createClaim(Player owner, Location claimChestLocation) {
        Claim claim = new Claim(owner, claimChestLocation);
        claims.put(claim.getClaimID(), claim);
        return claim;
    }

    public void saveClaims() {
        claims.values().forEach(Claim::saveClaim);
    }

    public void teleportOutOfClaim(Player user) {
        Random random = new Random();
        World world = user.getWorld();
        for (int i = 0; i < 32; ++i) {
            double x = user.getX() + ( random.nextDouble() - (double) 0.5F) * 50.0;
            double z = user.getZ() + (random.nextDouble() - (double) 0.5F) * 50.0;
            double y = Math.clamp(user.getY() + (double) (random.nextInt(16) - 8), world.getSeaLevel(), world.getHighestBlockYAt((int) x, (int) z));

            Location loc = new Location(world, x, y, z);

            if (!world.getWorldBorder().isInside(loc)) continue;
            if (getClaim(loc) == null) continue;
            user.teleportAsync(loc);
        }
    }

    public boolean isTooCloseToEnemyClaim(Chunk chunk, @Nullable Claim claim) {
        return isTooCloseToEnemyClaim(ChunkPos.of(chunk), claim);
    }

    public boolean isTooCloseToEnemyClaim(Location loc, @Nullable Claim claim) {
        return isTooCloseToEnemyClaim(ChunkPos.of(loc), claim);
    }

    public boolean isTooCloseToEnemyClaim(@NotNull ChunkPos pos, @Nullable Claim claim) {
        for (int x = -CLAIM_DISTANCE; x <= CLAIM_DISTANCE; x++) {
            for (int z = -CLAIM_DISTANCE; z <= CLAIM_DISTANCE; z++) {
                Claim nearbyClaim = chunkIndex.get(new ChunkPos(pos.world(), pos.x() + x, pos.z() + z));
                if (nearbyClaim != null && nearbyClaim != claim) {
                    return true;
                }
            }
        }
        return false;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void addShieldTask(@NotNull Claim claim) {

    }

}
