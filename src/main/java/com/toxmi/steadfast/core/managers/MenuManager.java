package com.toxmi.steadfast.core.managers;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.menu.Button;
import com.toxmi.steadfast.core.menu.Menu;
import com.toxmi.steadfast.core.menu.MenuSession;
import com.toxmi.steadfast.core.utils.Scheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuManager extends Scheduler {
    private static MenuManager instance;
    private static final Map<UUID, OpenMenu> OPEN = new ConcurrentHashMap<>();
    public MenuManager(Steadfast plugin) {
        super(plugin);
        instance = this;
    }

    public synchronized static MenuManager get() {
        if (instance == null) {
            instance = new MenuManager(Steadfast.get());
        }
        return instance;
    }

    public void openMenu(Player player, Menu menu) {
        playerScheduler(player, () -> {
            OpenMenu old = OPEN.get(player.getUniqueId());
            if (old != null && old.task != null) {
                old.task.cancel();
            }

            MenuSession session = new MenuSession();

            menu.rebuildBtns(player);
            Inventory inv = menu.createInv(player);
            render(player, menu, inv);

            OpenMenu openMenu = new OpenMenu(menu, session, inv);
            OPEN.put(player.getUniqueId(), openMenu);

            player.openInventory(inv);

            long ticks = menu.autoUpdateTicks();
            if (ticks > 0) {
                openMenu.task = playerScheduler(player, () -> {
                    OpenMenu current = OPEN.get(player.getUniqueId());

                    if (current == null) {
                        openMenu.task.cancel();
                        return;
                    }

                    if (OPEN.get(player.getUniqueId()) != current) {
                        openMenu.task.cancel();
                        return;
                    }

                    if (!player.getOpenInventory().getTopInventory().equals(current.inventory)) {
                        openMenu.task.cancel();
                        return;
                    }

                    current.menu.rebuildBtns(player);
                    render(player, current.menu, current.inventory);
                },  1, ticks);
            }
        });
    }


    private void render(Player player, Menu menu, Inventory inv) {
        for (var entry: menu.getButtons().entrySet()) {
            int slot = entry.getKey();
            if (slot < 0 || slot >= inv.getSize()) continue;
            Button btn = entry.getValue();
            if (btn == null) continue;
            inv.setItem(slot, btn.getItem(player));
        }
    }

    public OpenMenu getOpenMenu(Player player) {
        return OPEN.get(player.getUniqueId());
    }
    public void closeMenu(Player player) {
        OpenMenu openMenu = OPEN.get(player.getUniqueId());
        if (openMenu == null) return;
        if (openMenu.task != null) openMenu.task.cancel();
        openMenu.menu.onClose(player);
        OPEN.remove(player.getUniqueId());
    }


    public static final class OpenMenu {
        public final Menu menu;
        public final MenuSession session;
        public final Inventory inventory;
        public ScheduledTask task;

        OpenMenu(Menu menu, MenuSession session, Inventory inventory) {
            this.menu = menu;
            this.session = session;
            this.inventory = inventory;
        }
    }
}