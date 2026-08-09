package com.toxmi.steadfast.customenchants.customs;

import com.toxmi.steadfast.customenchants.CustomEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static com.toxmi.steadfast.utils.Potion.addPotionEffect;

public class Heavenly extends CustomEnchant {
    @Override
    public void useAbility(Player player, @Nullable Event event) {
        if (!(event instanceof EntityDeathEvent e)) return;
        player.sendMessage("yes");
        if (cm.isOnCooldown("heavenly", player.getUniqueId())) return;
        e.setCancelled(true);
        player.setHealth(20);
        cm.addCooldown("heavenly", player.getUniqueId());
        player.clearActivePotionEffects();
        addPotionEffect(PotionEffectType.REGENERATION, player, 45, 2);
        addPotionEffect(PotionEffectType.FIRE_RESISTANCE, player, 40, 1);
        addPotionEffect(PotionEffectType.ABSORPTION, player, 5, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 50,3,3 ,3);
        ItemStack item = player.getInventory().getHelmet();
        assert item != null;
        ItemMeta meta =  item.getItemMeta();
        if (!(meta instanceof Damageable itemMeta))  return;
        int maxDura = item.getType().getMaxDurability();
        int newDura = maxDura - itemMeta.getDamage() - (2 * maxDura / 3);
        if (newDura < 0) {
            player.getInventory().setHelmet(null);
        } else {
            itemMeta.setDamage(newDura);
            item.setItemMeta(itemMeta);
        }
    }
}
