package com.toxmi.steadfast;

import com.toxmi.steadfast.core.commands.GiveCustomCommand;
import com.toxmi.steadfast.core.listeners.MenuListener;
import com.toxmi.steadfast.core.managers.DatabaseManager;
import com.toxmi.steadfast.core.utils.Scheduler;
import com.toxmi.steadfast.modules.claims.ClaimManager;
import com.toxmi.steadfast.modules.claims.listeners.ClaimProtectionListener;
import com.toxmi.steadfast.modules.customenchants.CustomListener;
import com.toxmi.steadfast.modules.customenchants.CustomManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Steadfast extends JavaPlugin {
    private static Steadfast instance;
    private CustomListener customListener;
    private CustomManager customManager;
    private Scheduler scheduler;
    private DatabaseManager dbm;
    private String databaseType;
    private ClaimManager claimManager;

    private FileConfiguration config;

    public static Steadfast get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveResource("customs.yml", false);
        // Plugin startup logic
        this.scheduler = new Scheduler(this);
        this.config = getConfig();
        if (this.config.getBoolean("database.enabled")) {


            this.databaseType = getConfig().getString("database.type");

            this.dbm = new DatabaseManager(this.databaseType);
            this.dbm.connect();
            this.dbm.initTables();
        }
        initManagers();
        initListeners();
        initCommands();
    }

    @Override
    public void onDisable() {
        this.claimManager.saveClaims();
        this.getServer().getScheduler().cancelTasks(this);
        this.dbm.close();
    }

    private void initManagers() {
        this.claimManager = new ClaimManager();
        this.claimManager.loadClaims();
        this.customManager = new CustomManager();
    }

    private void initListeners() {
        this.customListener = new CustomListener(this);
        registerListener(this.customListener);
        registerListener(new MenuListener(this));
        registerListener(new ClaimProtectionListener());
    }

    private void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void initCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, c -> {
            c.registrar().register(new GiveCustomCommand().node());
        });
    }

    public String getDatabaseType() {
        return this.databaseType;
    }

    public CustomListener getCustomListener() {
        return this.customListener;
    }
}
