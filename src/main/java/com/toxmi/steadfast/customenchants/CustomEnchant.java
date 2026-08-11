package com.toxmi.steadfast.customenchants;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.utils.Scheduler;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public abstract class CustomEnchant extends Scheduler {
    protected final CustomManager cm;
    protected final Steadfast plugin;
    protected final CustomListener cl;

    public CustomEnchant() {
        super(Steadfast.get());
        this.plugin = Steadfast.get();
        cm = CustomManager.get();
        cl = CustomListener.get();
    }

    protected String getPDC(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) return "";
        return item.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public abstract void useAbility(Player player, @Nullable Event event);
}
