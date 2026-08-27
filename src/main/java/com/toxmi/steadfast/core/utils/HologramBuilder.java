package com.toxmi.steadfast.core.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HologramBuilder {

    private static final NamespacedKey indexKey = Keys.holoIndexKey;

    public static List<TextDisplay> createHolo(@NotNull Location location, NamespacedKey key, String keyValue, List<Component> lines) {
        World world = location.getWorld();
        List<TextDisplay> displays = new ArrayList<>();
        double offset = 2;
        int i = 0;
        for (Component line : lines) {
            int lineIndex = i;
            TextDisplay display = world.spawn(location.clone().add(0.5, offset, 0.5), TextDisplay.class, entity -> {

                entity.text(line);
                entity.setBillboard(Display.Billboard.CENTER);
                entity.setSeeThrough(false);
                entity.setShadowed(true);
                entity.setDefaultBackground(false);
                entity.setBackgroundColor(org.bukkit.Color.fromARGB(0, 0, 0, 0));
                entity.setViewRange(2.0f);
                entity.setLineWidth(200);
                entity.setBrightness(new Display.Brightness(15, 15));
                entity.setInterpolationDuration(0);
                entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, keyValue);
                entity.getPersistentDataContainer().set(indexKey, PersistentDataType.INTEGER, lineIndex);
            });
            offset += 0.3;
            displays.add(display);
            i++;
        }
        return displays;
    }

    public static void setLine(@NotNull Location location, NamespacedKey key, String keyValue, double radius, int index, Component newText) {
        for (TextDisplay entity : location.getNearbyEntitiesByType(TextDisplay.class, radius)) {

            String value = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            Integer indexVal = entity.getPersistentDataContainer().get(indexKey, PersistentDataType.INTEGER);

            if (value != null && value.equals(keyValue) && indexVal != null && indexVal == index) {
                entity.text(newText);
            }
        }
    }


    public static void destroyHolo(@NotNull Location location, NamespacedKey key, String keyValue, double radius) {

        for (TextDisplay entity : location.getNearbyEntitiesByType(TextDisplay.class, radius)) {
            String value = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (value != null && value.equals(keyValue)) {
                entity.remove();
            }
        }
    }
}
