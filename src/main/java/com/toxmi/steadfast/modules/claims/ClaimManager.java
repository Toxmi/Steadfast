package com.toxmi.steadfast.modules.claims;

import com.toxmi.steadfast.core.managers.DatabaseManager;
import com.toxmi.steadfast.core.utils.SQL;
import com.toxmi.steadfast.core.utils.Scheduler;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimManager {
    private static ClaimManager instance;
    private final Scheduler sch;
    private final DatabaseManager db;
    private final Map<UUID, Claim> claims = new ConcurrentHashMap<>();


    public ClaimManager() {
        instance = this;
        this.sch = Scheduler.get();
        this.db = DatabaseManager.get();
    }

    public synchronized static ClaimManager get() {
        if (instance == null) {
            instance = new ClaimManager();
        }
        return instance;
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
        return claims.values().stream().filter(claim -> claim.ownsChunk(chunk)).findFirst().orElse(null);
    }

    public Claim getClaim(Location location) {
        return claims.values().stream().filter(claim -> claim.ownsChunk(location)).findFirst().orElse(null);
    }

    public Claim getClaimByPlayer(UUID player) {
        return claims.values().stream().filter(claim -> claim.isMember(player)).findFirst().orElse(null);
    }

    public int getClaimRank(Claim claim) {
        List<Claim> sorted = claims.values().stream()
                .sorted(Comparator.comparingInt(Claim::getPower).reversed())
                .toList();

        int index = sorted.indexOf(claim);

        return index == -1 ? -1 : index + 1;
    }

    public void deleteClaim(UUID claimID) {
        claims.remove(claimID).deleteClaim();
    }

    public void addClaim(Player owner, Location claimChestLocation) {
        Claim claim = new Claim(owner, claimChestLocation);
        claims.put(claim.getClaimID(), claim);
    }

    public void saveClaims() {
        claims.values().forEach(Claim::saveClaim);
    }
}
