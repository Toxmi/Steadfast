package com.toxmi.steadfast.core.utils;

import com.toxmi.steadfast.Steadfast;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class Keys {
    private static final Steadfast plugin = Steadfast.get();
    public static NamespacedKey customKey = new NamespacedKey(plugin, "custom");
    public static NamespacedKey arrowForceKey = new NamespacedKey(plugin, "arrowForce");

    // Claims

    public static NamespacedKey claimKey = new NamespacedKey(plugin, "claim");
    public static NamespacedKey claimHoloKey = new NamespacedKey(plugin, "claim_holo");

    // Items

    public static NamespacedKey itemKey = new NamespacedKey(plugin, "item");


    // Misc

    public static NamespacedKey holoIndexKey = new NamespacedKey(plugin, "line_index");

    public static boolean hasKey(ItemStack item, NamespacedKey key) {
        return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    public static String getKey(ItemStack item, NamespacedKey key) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, "");
    }
}
