package com.toxmi.steadfast.core.managers;

import com.toxmi.steadfast.Steadfast;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MessageManager {
    private static MessageManager instance;
    private final Map<String, String> messages = new ConcurrentHashMap<>();
    private final Steadfast plugin;
    private FileConfiguration config;
    private File file;

    public MessageManager() {
        instance = this;
        this.plugin = Steadfast.get();
        load();
    }

    public synchronized static MessageManager get() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        loadConfig();
    }

    private void loadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        messages.clear();
        for (String key : Objects.requireNonNull(config.getConfigurationSection("messages")).getKeys(false)) {
            for (String message : Objects.requireNonNull(config.getConfigurationSection("messages." + key)).getKeys(false)) {
                String fullPath = "messages." + key + "." + message;
                messages.put(fullPath, config.getString(fullPath));
            }
        }
    }

    public void reload() {
        loadConfig();
    }

    public String get(String path) {
        return messages.getOrDefault(path, "<Red>ERROR: Message not found! Please contact the admins</Red><br>" + path);
    }
}
