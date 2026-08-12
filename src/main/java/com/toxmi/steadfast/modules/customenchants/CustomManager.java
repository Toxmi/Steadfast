package com.toxmi.steadfast.modules.customenchants;

import com.toxmi.steadfast.Steadfast;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomManager {
    private final Map<String, Object[]> customs = new HashMap<>();
    private final Steadfast plugin = Steadfast.get();

    private final Map<Cooldown, Long> cooldowns = new HashMap<>();
    private static CustomManager instance;
    private File file;
    private FileConfiguration config;

    public CustomManager() {
        registerCustoms();
    }

    private void registerCustoms() {
        file = new File(plugin.getDataFolder(), "customs.yml");

        if (!file.exists()) {
            plugin.saveResource("customs.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        load();

    }

    public void load() {
        for (String custom : config.getConfigurationSection("customs").getKeys(false)) {
            Object[] vars = new Object[5];
            if (!config.getBoolean("customs." + custom + ".enabled")) continue;
            vars[0] = config.getDouble("customs." + custom + ".cooldown");
            vars[1] = Material.getMaterial(config.getString("customs." + custom + ".material"));
            vars[2] = config.getDouble("customs." + custom + ".variable1");
            vars[3] = config.getDouble("customs." + custom + ".variable2");
            vars[4] = config.getDouble("customs." + custom + ".variable3");
            customs.put(custom, vars);
        }
    }

    public static CustomManager get() {
        if (instance == null) {
            instance = new CustomManager();
        }
        return instance;
    }



    public record Cooldown(UUID player, String custom) {}

    public boolean isOnCooldown(String custom, UUID player) {
        Long time = cooldowns.getOrDefault(new Cooldown(player, custom),0L);
        return time + getCooldown(custom) * 1000 > System.currentTimeMillis();
    }

    public void addCooldown(String custom, UUID player) {
        cooldowns.put(new Cooldown(player, custom), System.currentTimeMillis());
    }

    public void removeCooldown(String custom, UUID player) {
        cooldowns.remove(new Cooldown(player, custom));
    }


    public double getCooldown(String custom) {
        return (double) customs.get(custom)[0];
    }

    public double getVar1(String custom) {
        return (double) customs.get(custom)[2];
    }

    public double getVar2(String custom) {
        return (double) customs.get(custom)[3];
    }

    public double getVar3(String custom) {
        return (double) customs.get(custom)[4];
    }

    public Material getMaterial(String custom) {
        return (Material) customs.get(custom)[1];
    }





}
