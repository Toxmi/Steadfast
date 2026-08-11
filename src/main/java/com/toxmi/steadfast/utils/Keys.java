package com.toxmi.steadfast.utils;

import com.toxmi.steadfast.Steadfast;
import org.bukkit.NamespacedKey;

public class Keys {
    private static final Steadfast plugin = Steadfast.get();
    public static NamespacedKey customKey = new NamespacedKey(plugin, "custom");
    public static NamespacedKey arrowForceKey = new NamespacedKey(plugin, "arrowForce");
}
