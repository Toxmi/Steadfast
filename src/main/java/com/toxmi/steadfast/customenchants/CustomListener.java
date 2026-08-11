package com.toxmi.steadfast.customenchants;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.customenchants.customs.UnbrokenChain;
import com.toxmi.steadfast.utils.Keys;
import com.toxmi.steadfast.utils.Scheduler;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CustomListener  implements Listener {
    private static CustomListener instance;
    private final Steadfast plugin;
    private final Scheduler sch;
    private final Map<String, CustomEnchant> customs = new HashMap<>();
    private final Set<UUID> disabledCustoms = new HashSet<>();
    private ScheduledTask task;
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
        instance = this;
        this.plugin = plugin;
        this.sch = Scheduler.get();
        registerCustoms();
        tick();
    }

    public synchronized static CustomListener get() {
        if (instance == null) {
            instance = new CustomListener(Steadfast.get());
        }
        return instance;
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
            callCustom(attacker, item, event);

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        List<String> items = getArmorCustoms(p);
        for (String item : items) {
            callCustom(p, item, event);
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

        callCustom(player, pdc, event);

        if (!(event.getDamager() instanceof Player attacker)) return;
        ItemStack item = attacker.getInventory().getItemInMainHand();
        String pdc2 = getPDC(item, Keys.customKey);
        callCustom(attacker, pdc2, event);
    }

    @EventHandler
    void onResurrect(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCancelled()) return;
        if (player.getInventory().getHelmet() == null) return;
        if (getPDC(player.getInventory().getHelmet(), Keys.customKey).equalsIgnoreCase("heavenly")) {
            callCustom(player, "heavenly", event);
        }
    }


    @EventHandler
    void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof Player attacker)) return;
        ItemStack item = attacker.getInventory().getItemInMainHand();
        String pdc = getPDC(item, Keys.customKey);
        callCustom(attacker, pdc, event);
    }

    @EventHandler
    void onItemDamage (PlayerItemDamageEvent event) {
        if (!getPDC(event.getItem(), Keys.customKey).equalsIgnoreCase("reinforce")) return;
        callCustom(event.getPlayer(), "reinforce", event);
    }

    @EventHandler
    void onBlockBreak (BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        callCustom(player, getPDC(item, Keys.customKey), event);
    }

    @EventHandler
    void onProjectileShoot (PlayerLaunchProjectileEvent event) {
        String pdc = getPDC(event.getItemStack(), Keys.customKey);
        setPDC(event.getProjectile(), pdc, Keys.customKey);
        callCustom(event.getPlayer(), pdc, event);
    }

    @EventHandler
    void onProjectileHit (ProjectileHitEvent event) {
        String pdc = getPDC(event.getEntity(), Keys.customKey);
        callCustom(null, pdc, event);
    }

    @EventHandler
    void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        String pdc = getPDC(event.getBow(), Keys.customKey);
        setPDC(event.getEntity(), pdc, Keys.customKey);
        callCustom(player, pdc, event);
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

    private void setPDC(Entity item, String value, NamespacedKey key) {
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

    private void callCustom(@Nullable Player player, @NotNull String custom, @Nullable Event event) {
        if (player != null) {
            if (disabledCustoms.contains(player.getUniqueId())) return;
        }
        if (customs.containsKey(custom)) {
            customs.get(custom).useAbility(player, event);
        }
    }

    private void tick() {
        task = sch.globalRegion(() -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                sch.playerScheduler(player, () -> {
                    List<String> items = getCustoms(player);
                    for (String item : items) {
                        callCustom(player, item, null);
                    }
                });
            }
        }, 1, 20);
    }

    public void disableCustoms(UUID player) {
        disabledCustoms.add(player);
    }

    public void enableCustoms(UUID player) {
        disabledCustoms.remove(player);
    }

    public List<String> getCustomsList() {
        return customsList;
    }
}
