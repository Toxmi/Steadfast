package com.toxmi.steadfast.core.utils;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.managers.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.util.UUID;

public class User {
    private final Steadfast plugin;
    private final Scheduler sch;
    private final DatabaseManager db;

    private final UUID uuid;
    private final String name;
    private final OfflinePlayer offlinePlayer;

    private double coinBalance = 0;
    private int kills = 0;
    private int deaths = 0;
    private int killstreak = 0;
    private Duration playtime = Duration.ZERO;
    private Long lastPlayTimeSync = System.currentTimeMillis();

    public User(UUID uuid, double coinBalance, int kills, int deaths, int killstreak, Duration playtime) {
        this.plugin = Steadfast.get();
        this.sch = Scheduler.get();
        this.db = DatabaseManager.get();
        this.uuid = uuid;
        this.offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
        this.name = offlinePlayer.getName();
        this.coinBalance = coinBalance;
        this.kills = kills;
        this.deaths = deaths;
        this.killstreak = killstreak;
        this.playtime = playtime;
    }

    public User(UUID uuid) {
        this.plugin = Steadfast.get();
        this.sch = Scheduler.get();
        this.db = DatabaseManager.get();
        this.uuid = uuid;
        this.offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
        this.name = offlinePlayer.getName();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public double getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(double coinBalance) {
        this.coinBalance = coinBalance;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_COIN_BALANCE, coinBalance, this.toString());
        });
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_STATS_KILLS, kills, this.toString());
        });
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_STATS_DEATHS, deaths, this.toString());
        });
    }

    public int getKillstreak() {
        return killstreak;
    }

    public void setKillstreak(int killstreak) {
        this.killstreak = killstreak;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_STATS_KILL_STREAK, killstreak, this.toString());
        });
    }

    public Duration getPlayTime() {
        long now = System.currentTimeMillis();
        long diff = now - lastPlayTimeSync;
        playtime = playtime.plusSeconds(diff / 1000);
        lastPlayTimeSync = now;
        return playtime;
    }

    public void addCoinBalance(double coinBalance) {
        this.coinBalance += coinBalance;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_COIN_BALANCE, coinBalance, this.toString());
        });
    }

    public void addKills(int kills) {
        this.kills += kills;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_STATS_KILLS, kills, this.toString());
        });
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_STATS_DEATHS, deaths, this.toString());
        });
    }

    public void addKillstreak(int killstreak) {
        this.killstreak += killstreak;
        sch.async(() -> {
            db.executeUpdate(SQL.UPDATE_PLAYER_STATS_KILL_STREAK, killstreak, this.toString());
        });
    }

    public OfflinePlayer get() {
        return offlinePlayer;
    }

    public boolean hasMoney(double amount) {
        return coinBalance >= amount;
    }



    @Override
    public String toString() {
        return uuid.toString();
    }

}
