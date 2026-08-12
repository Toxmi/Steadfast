package com.toxmi.steadfast.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    private static ItemBuilder instance;
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public static synchronized ItemBuilder get() {
        if (ItemBuilder.instance == null) {
            ItemBuilder.instance = new ItemBuilder();
        }
        return ItemBuilder.instance;
    }

    private static Component cc(String text) {
        if (text == null) return Component.empty();
        return LEGACY.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    public static class CustomItem {
        private final ItemStack item;
        private ItemMeta meta;
        private final List<Component> lore = new ArrayList<>();


        public CustomItem(Material material, int amount) {
            item = new ItemStack(material, amount);
            meta = item.getItemMeta();
        }

        /**
         * @param displayName Sets the name of the item.
         */
        public CustomItem displayName(Component displayName) {
            if (meta == null) meta = item.getItemMeta();
            meta.displayName(displayName);
            return this;
        }

        /**
         * @param displayName Sets the name of the item.
         */
        public CustomItem displayName(String displayName) {
            if (meta == null) meta = item.getItemMeta();
            meta.displayName(cc(displayName));
            return this;
        }

        /**
         * Note: Adds unbreaking 1 enchantment.
         *
         * @param glint Whether the item should have an enchantment glint.
         */
        public CustomItem glint(boolean glint) {
            if (meta == null) meta = item.getItemMeta();
            if (glint) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            return this;
        }

        /**
         * @param lines Lore of the item.
         */
        public CustomItem lore(Component... lines) {
            if (meta == null) meta = item.getItemMeta();
            if (lines != null) lore.addAll(List.of(lines));
            return this;
        }

        /**
         * @param lines Lore of the item.
         */
        public CustomItem lore(String... lines) {
            if (meta == null) meta = item.getItemMeta();
            if (lines != null) {
                for (String s : lines) lore.add(cc(s));
            }
            return this;
        }

        /**
         * @param lines Lore of the item.
         */
        public CustomItem loreC(List<Component> lines) {
            if (meta == null) meta = item.getItemMeta();
            if (lines != null) {
                lore.addAll(lines);
            }
            return this;
        }


        public CustomItem lore(List<String> lines) {
            if (meta == null) meta = item.getItemMeta();
            if (lines != null) {
                for (String s : lines) lore.add(cc(s));
            }
            return this;
        }

        /**
         * Sets a persistent data container value.
         *
         * @param key   NamespacedKey of the value.
         * @param value What the value will be set to.
         */
        public CustomItem pdcString(NamespacedKey key, String value) {
            if (meta == null) meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
            return this;
        }

        public CustomItem pdcInt(NamespacedKey key, int value) {
            if (meta == null) meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
            return this;
        }

        public CustomItem pdcBoolean(NamespacedKey key, boolean value) {
            if (meta == null) meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, value);
            return this;
        }

        /**
         * Adds an enchantment to the item.
         *
         * @param enchantment Enchantment that will be added.
         * @param level       Level of the enchantment.
         */
        public CustomItem enchant(Enchantment enchantment, int level) {
            if (meta == null) meta = item.getItemMeta();
            meta.addEnchant(enchantment, level, true);
            return this;
        }

        public CustomItem setEnchants(Map<Enchantment, Integer> enchants) {
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                enchant(e.getKey(), e.getValue());
            }
            return this;
        }

        /**
         * Sets the durability of the item.
         *
         * @param durability The durability of the item.
         */
        public CustomItem durability(int durability) {
            item.setDurability((short) durability);
            return this;
        }

        /**
         * Sets the max stack size of the item.
         *
         * @param maxStackSize Sets the max stack size of the item. Max 99
         */
        public CustomItem maxStackSize(int maxStackSize) {
            if (maxStackSize > 99) maxStackSize = 99;
            meta.setMaxStackSize(maxStackSize);
            return this;
        }

        /**
         * Hides the tooltips of the item
         *
         * @param hide Whether to hide tooltips of an item.
         */
        public CustomItem hideComponent(boolean hide) {
            meta.setHideTooltip(hide);
            return this;
        }

        /**
         * Makes the item unbreakable.
         */
        public CustomItem unbreakable() {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            return this;
        }

        /**
         * Sets the meta to a predefined ItemMeta.
         *
         * @param meta Predefined ItemMeta.
         */
        public CustomItem setMeta(ItemMeta meta) {
            this.meta = meta;
            return this;
        }


        /**
         * Sets the item model key.
         *
         * @param key Item model key.
         */
        public CustomItem itemModel(NamespacedKey key) {
            meta.setItemModel(key);
            return this;
        }


        /**
         * Creates the item.
         *
         * @return Returns the item.
         */
        public ItemStack build() {
            meta.lore(lore);
            item.setItemMeta(meta);
            return item;
        }

    }

    public CustomItem customItem(Material material, int amount) {
        return new CustomItem(material, amount);
    }

    public CustomItem customItem(Material material) {
        return new CustomItem(material, 1);
    }

    public ItemStack getFiller() {

        return customItem(Material.GRAY_STAINED_GLASS_PANE,1)
                .displayName(Component.text(""))
                .hideComponent(true)
                .build();
    }
    public ItemStack getGoBack() {
        return customItem(Material.RED_STAINED_GLASS_PANE,1)
                .displayName(Component.text("Return").color(NamedTextColor.RED))
                .lore(Component.text("Click to go back").color(NamedTextColor.GRAY))
                .build();
    }
    public ItemStack getNextPage() {
        return customItem(Material.ARROW,1)
                .displayName(Component.text("Next Page").color(NamedTextColor.GREEN))
                .build();
    }
    public ItemStack getPreviousPage() {
        return customItem(Material.ARROW,1)
                .displayName(Component.text("Previous Page").color(NamedTextColor.GREEN))
                .build();
    }
}