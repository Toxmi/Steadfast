package com.toxmi.steadfast.core.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface Button {
    ItemStack getItem(Player player);
    default void onButtonClick(Player player, ClickType clickType, InventoryClickEvent event) {}

}
