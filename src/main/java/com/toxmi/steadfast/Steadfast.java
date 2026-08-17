package com.toxmi.steadfast;

import com.toxmi.steadfast.core.commands.GiveCustomCommand;
import com.toxmi.steadfast.core.listeners.MenuListener;
import com.toxmi.steadfast.core.managers.DatabaseManager;
import com.toxmi.steadfast.core.utils.Scheduler;
import com.toxmi.steadfast.modules.customenchants.CustomListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Steadfast extends JavaPlugin {
    private static Steadfast instance;
    private CustomListener customListener;
    private Scheduler scheduler;
    private DatabaseManager dbm;
    private String databaseType;

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
        if (config.getBoolean("database.enabled")) {


            databaseType = getConfig().getString("database.type");

            this.dbm = new DatabaseManager(databaseType);
            dbm.connect();
            dbm.initTables();
        }
        initListeners();
        initCommands();
    }

    @Override
    public void onDisable() {
        dbm.close();
    }

    private void initListeners() {
        customListener = new CustomListener(this);
        registerListener(customListener);
        registerListener(new MenuListener(this));
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
        return databaseType;
    }

    public CustomListener getCustomListener() {
        return customListener;
    }
}
