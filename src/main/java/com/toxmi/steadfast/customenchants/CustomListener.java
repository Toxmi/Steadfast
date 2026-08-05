package com.toxmi.steadfast.customenchants;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.customenchants.customs.UnbrokenChain;
import com.toxmi.steadfast.utils.Keys;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
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
            "Demolotionist",
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
        List<ItemStack> attackerItems = getCustoms(attacker);
        for (ItemStack item : attackerItems) {
            String pdc = getPDC(item, Keys.customKey);
            if (!customs.containsKey(pdc)) continue;
            customs.get(pdc).useAbility(attacker, event);

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        List<ItemStack> items = getArmorCustoms(p);
        for (ItemStack item : items) {
            String pdc = getPDC(item, Keys.customKey);
            if (!customs.containsKey(pdc)) continue;
            customs.get(pdc).useAbility(p, event);

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
    void onResurrect(EntityResurrectEvent event) {
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

    private List<ItemStack> getArmorCustoms(Player player) {
        List<ItemStack> customs  = new ArrayList<>();
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) continue;
            String pdc = getPDC(item, Keys.customKey);
            if (!pdc.isEmpty()) customs.add(item);
        }
        return customs;
    }

    private List<ItemStack> getCustoms(Player player) {
        List<ItemStack> customs = new ArrayList<>(getArmorCustoms(player));
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            String pdc = getPDC(player.getInventory().getItemInMainHand(), Keys.customKey);
            if (!pdc.isEmpty()) customs.add(player.getInventory().getItemInMainHand());
        }
        if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
            String pdc = getPDC(player.getInventory().getItemInOffHand(), Keys.customKey);
            if (!pdc.isEmpty()) customs.add(player.getInventory().getItemInOffHand());
        }
        return customs;
    }

    public String getPDC(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) return "";
        return item.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    private void callEvent(Event event) {
        plugin.getServer().getPluginManager().callEvent(event);
    }

    private void removeChain(Player victim) {
        UnbrokenChain chain = (UnbrokenChain) customs.get("unbrokenchain");
        chain.removeChain(victim);
    }

    public List<String> getCustomsList() {
        return customsList;
    }
}
