package com.toxmi.steadfast.core.menu;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.managers.MenuManager;
import com.toxmi.steadfast.core.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class Menu implements InventoryHolder {
    protected final Map<Integer, Button> buttons = new HashMap<>();
    protected Steadfast plugin = Steadfast.get();
    private final MenuManager manager = MenuManager.get();
    protected ItemBuilder ib = ItemBuilder.get();
    private Inventory inv;

    public abstract void onOpen(@Nullable Player player);

    public abstract Component getTitle(@Nullable Player player);

    public abstract int getSize(@Nullable Player player);

    public void onClose(Player player) {
    }

    ;

    public boolean cancelClick() {
        return true;
    }

    public long autoUpdateTicks() {
        return 0L;
    }

    ;

    public final void openMenu(Player player) {
        manager.openMenu(player, this);
    }

    public final void setButton(int slot, Button button) {
        buttons.put(slot, button);
    }

    public final Button getButton(int slot) {
        return buttons.get(slot);
    }

    public final Map<Integer, Button> getButtons() {
        return buttons;
    }

    public final Inventory createInv(Player player) {
        int size = Math.clamp((getSize(player) / 9) * 9, 9, 54);
        this.inv = Bukkit.createInventory(this, size, getTitle(player));
        return this.inv;
    }

    public final void rebuildBtns(Player player) {
        buttons.clear();
        onOpen(player);
    }

    public boolean allowShiftClick() {
        return false;
    }

    public final void refresh(Player player) {
        if (inv == null) return;
        buttons.clear();
        onOpen(player);
        for (Map.Entry<Integer, Button> e : buttons.entrySet()) {
            inv.setItem(e.getKey(), e.getValue().getItem(player));
        }
        player.updateInventory();
    }

    public final void fill() {
        ItemStack filler = ib.getFiller();
        for (int i = 0; i < getSize(null); i++) {
            setButton(i, new Button() {
                @Override
                public ItemStack getItem(Player player) {
                    return filler;
                }

                @Override
                public void onButtonClick(Player player, ClickType clickType, InventoryClickEvent event) {
                    event.setCancelled(true);
                }
            });
        }
    }


    public Inventory getInv(Player player) {
        return inv;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv != null ? inv : Bukkit.createInventory(this, 9);
    }
}