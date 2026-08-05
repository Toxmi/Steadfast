package com.toxmi.steadfast;

import com.toxmi.steadfast.commands.GiveCustomCommand;
import com.toxmi.steadfast.customenchants.CustomListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Steadfast extends JavaPlugin {
    private static Steadfast instance;
    private CustomListener customListener;

    @Override
    public void onEnable() {
        instance = this;
        saveResource("customs.yml", false);
        // Plugin startup logic
        initListeners();
        initCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initListeners() {
        customListener = new CustomListener(this);
        registerListener(customListener);
    }

    private void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void initCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, c -> {
            c.registrar().register(new GiveCustomCommand().node());
        });
    }

    public static Steadfast get() {
        return instance;
    }

    public CustomListener getCustomListener() {
        return customListener;
    }
}
