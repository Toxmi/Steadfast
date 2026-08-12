package com.toxmi.steadfast.core.listeners;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.managers.MenuManager;
import com.toxmi.steadfast.core.menu.Button;
import com.toxmi.steadfast.core.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {
    private final MenuManager manager;
    private final Map<UUID, Long> clickDebounce = new HashMap<>();
    private static final long clickDelay = 100L;
    public MenuListener(Steadfast plugin) {
        this.manager = MenuManager.get();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;

        MenuManager.OpenMenu open = manager.getOpenMenu(player);
        if (open == null) return;
        if (event.getView().getTopInventory() != open.inventory) return;



        if (event.isShiftClick() && !menu.allowShiftClick()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != null &&
                event.getClickedInventory() == event.getView().getTopInventory()) {
            if (menu.cancelClick()) {
                event.setCancelled(true);
            }

            long now = System.currentTimeMillis();
            long last = clickDebounce.getOrDefault(player.getUniqueId(), 0L);
            if (now - last < clickDelay) {
                event.setCancelled(true);
                return;
            }
            clickDebounce.put(player.getUniqueId(), now);
            Button button = menu.getButton(event.getSlot());
            if (button != null) {
                button.onButtonClick(player, event.getClick(), event);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;
        if(!(event.getView().getTopInventory().getHolder() instanceof Menu)) return;
        MenuManager.OpenMenu open = manager.getOpenMenu(player);
        if(open == null) return;
        if(event.getView().getTopInventory() != open.inventory) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        if(!(event.getView().getTopInventory().getHolder() instanceof Menu)) return;
        MenuManager.OpenMenu open = manager.getOpenMenu(player);
        if(open == null) return;
        if(event.getView().getTopInventory() != open.inventory) return;
        manager.closeMenu(player);
    }
}