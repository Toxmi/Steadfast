package com.toxmi.steadfast.customenchants;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.customenchants.customs.UnbrokenChain;
import com.toxmi.steadfast.utils.Keys;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CustomListener  implements Listener {
    private final Steadfast plugin;
    private final Map<String, CustomEnchant> customs = new HashMap<>();
    private final List<String> customsList = List.of(
            "Adrenaline",
            "Artemis",
            "Ascent",
            "Athletic",
            "AutoloadingHolster",
            "Berserk",
            "Bolt",
            "Bulwark",
            "Clapback",
            "Cleaving",
            "Compound",
            "Concuss",
            "Counter",
            "Critical",
            "Deflector",
            "Delusional",
            "Demolitionist",
            "Endurance",
            "Extinguish",
            "Firefly",
            "FirstStrike",
            "Fling",
            "Frost",
            "Hardhat",
            "Harmonic",
            "Headless",
            "Heavenly",
            "Homing",
            "Hook",
            "Immunity",
            "Impale",
            "Infernal",
            "Knockout",
            "Lifebloom",
            "Lifesteal",
            "LowGround",
            "Ninja",
            "Quarry",
            "Rally",
            "Reaper",
            "Reinforce",
            "Sandpaper",
            "Shock",
            "Slash",
            "Sustenance",
            "Tank",
            "Trickster",
            "UnbrokenChain",
            "Underdog",
            "Unstable",
            "Volley",
            "Vortex",
            "Weightless"
    );

    public CustomListener(Steadfast plugin) {
        this.plugin = plugin;
        registerCustoms();
        tick();
    }

    private void registerCustoms() {
        for (String custom : customsList) {
            try {
                customs.put(custom.toLowerCase(), (CustomEnchant) Class.forName("com.toxmi.steadfast.customenchants.customs." + custom).getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 31.7.2026 - I'll make this better tomorrow
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamageSource().getCausingEntity() instanceof Player attacker)) return;
        List<String> attackerItems = getCustoms(attacker);
        for (String item : attackerItems) {
            if (!customs.containsKey(item)) continue;
            customs.get(item).useAbility(attacker, event);

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        List<String> items = getArmorCustoms(p);
        for (String item : items) {
            if (!customs.containsKey(item)) continue;
            customs.get(item).useAbility(p, event);
        }
        removeChain(p);
    }

    @EventHandler
    public void onShieldDisable(PlayerShieldDisableEvent event) {
        Player player = event.getPlayer();
        ItemStack shield;
        if (player.getInventory().getItemInMainHand().getType() == Material.SHIELD) {
            shield = player.getInventory().getItemInMainHand();
        } else {
            shield = player.getInventory().getItemInOffHand();
        }
        String pdc = getPDC(shield, Keys.customKey);

        if (customs.containsKey(pdc)) {
            customs.get(pdc).useAbility(player, event);
        }

        if (!(event.getDamager() instanceof Player attacker)) return;
        ItemStack item = attacker.getInventory().getItemInMainHand();
        String pdc2 = getPDC(item, Keys.customKey);
        if (customs.containsKey(pdc2)) {
            customs.get(pdc2).useAbility(attacker, event);
        }
    }

    @EventHandler
    void onResurrect(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCancelled()) return;
        if (player.getInventory().getHelmet() == null) return;
        if (getPDC(player.getInventory().getHelmet(), Keys.customKey).equalsIgnoreCase("heavenly")) {
            CustomEnchant custom = customs.get("heavenly");
            if (custom != null) custom.useAbility(player, event);
        }
    }


    @EventHandler
    void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof Player attacker)) return;
        ItemStack item = attacker.getInventory().getItemInMainHand();
        if (item == null) return;
        String pdc = getPDC(item, Keys.customKey);
        if (!pdc.isEmpty()) {
            CustomEnchant custom = customs.get(pdc);
            if (custom != null) custom.useAbility(attacker, event);
        }
    }

    @EventHandler
    void onItemDamage (PlayerItemDamageEvent event) {
        if (!getPDC(event.getItem(), Keys.customKey).equalsIgnoreCase("reinforce")) return;
        customs.get("reinforce").useAbility(event.getPlayer(), event);
    }

    @EventHandler
    void onBlockBreak (BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (customs.containsKey(getPDC(item, Keys.customKey))) {
            customs.get(getPDC(item, Keys.customKey)).useAbility(player, event);
        }
    }

    @EventHandler
    void onProjectileShoot (PlayerLaunchProjectileEvent event) {
        String pdc = getPDC(event.getItemStack(), Keys.customKey);
        setPDC(event.getProjectile(), pdc, Keys.customKey);
        if (customs.containsKey(pdc)) {
            customs.get(pdc).useAbility(event.getPlayer(), event);
        }
    }

    @EventHandler
    void onProjectileHit (ProjectileHitEvent event) {
        String pdc = getPDC(event.getEntity(), Keys.customKey);
        if (customs.containsKey(pdc)) {
            customs.get(pdc).useAbility(null, event);
        }
    }

    @EventHandler
    void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        String pdc = getPDC(event.getBow(), Keys.customKey);
        if (customs.containsKey(pdc)) {
            customs.get(pdc).useAbility(player, event);
        }
    }

    private List<String> getArmorCustoms(Player player) {
        List<String> customs  = new ArrayList<>();
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) continue;
            String pdc = getPDC(item, Keys.customKey);
            if (!pdc.isEmpty()) customs.add(pdc);
        }
        return customs;
    }

    private void setPDC(Projectile item, String value, NamespacedKey key) {
        item.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
    }

    private void setPDC(ItemStack item, String value, NamespacedKey key) {
        item.getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
    }

    private List<String> getCustoms(Player player) {
        List<String> customs = new ArrayList<>(getArmorCustoms(player));
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            String pdc = getPDC(player.getInventory().getItemInMainHand(), Keys.customKey);
            if (!pdc.isEmpty()) customs.add(pdc);
        }
        if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
            String pdc = getPDC(player.getInventory().getItemInOffHand(), Keys.customKey);
            if (!pdc.isEmpty()) customs.add(pdc);
        }
        return customs;
    }

    private String getPDC(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) return "";
        return item.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public String getPDC(Entity entity, NamespacedKey key) {
        if (entity == null|| !entity.getPersistentDataContainer().has(key, PersistentDataType.STRING)) return "";
        return entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }


    private void removeChain(Player victim) {
        UnbrokenChain chain = (UnbrokenChain) customs.get("unbrokenchain");
        chain.removeChain(victim);
    }

    private void tick() {
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                List<String> items = getCustoms(player);
                for (String item : items) {
                    if (!customs.containsKey(item)) continue;
                    customs.get(item).useAbility(player, null);
                }
            }
        },1,20);
    }

    public List<String> getCustomsList() {
        return customsList;
    }
}
