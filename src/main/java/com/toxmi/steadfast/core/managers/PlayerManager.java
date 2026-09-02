package com.toxmi.steadfast.core.managers;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.utils.SQL;
import com.toxmi.steadfast.core.utils.Scheduler;
import com.toxmi.steadfast.core.utils.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private static PlayerManager instance;
    private final DatabaseManager db;
    private final Scheduler sch;
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private Steadfast plugin;


    public PlayerManager(Steadfast plugin) {
        instance = this;
        this.plugin = plugin;
        this.db = DatabaseManager.get();
        this.sch = Scheduler.get();
        load();
    }

    public synchronized static PlayerManager get() {
        if (instance == null) {
            instance = new PlayerManager(Steadfast.get());
        }
        return instance;
    }

    private void load() {
        sch.async(() -> {
            try (ResultSet rs = db.executeResultStatement(SQL.GET_ALL_PLAYERS)) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    double balance = rs.getDouble("coinbalance");
                    int kills = rs.getInt("kills");
                    int deaths = rs.getInt("deaths");
                    int killStreak = rs.getInt("killstreak");
                    Duration playtime = Duration.ofSeconds(rs.getInt("playtime"));
                    users.put(uuid, new User(uuid, balance, kills, deaths, killStreak, playtime));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load all players" + e);
            }
        });
    }

    public void addUser(@NotNull Player player) {
        if (users.containsKey(player.getUniqueId())) return;
        sch.async(() -> {
            UUID uuid = player.getUniqueId();
            db.executeUpdate(SQL.INSERT_PLAYER, uuid.toString());
            db.executeUpdate(SQL.INSERT_PLAYER_STATS, uuid.toString());
            users.put(uuid, new User(uuid));
        });
    }

    public @Nullable User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public @Nullable User getUser(String name) {
        return users.values().stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public @Nullable User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

}
