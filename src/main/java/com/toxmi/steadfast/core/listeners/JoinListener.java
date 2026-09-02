package com.toxmi.steadfast.core.listeners;

import com.toxmi.steadfast.core.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final PlayerManager pm;

    public JoinListener() {
        this.pm = PlayerManager.get();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        pm.addUser(player);
    }
}
