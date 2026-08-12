package com.toxmi.steadfast.modules.customenchants.customs;

import com.toxmi.steadfast.modules.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.toxmi.steadfast.core.utils.Potion.addPotionEffect;
import static com.toxmi.steadfast.core.utils.Potion.removePotionEffect;

public class Athletic extends CustomEnchant {
    private static final long LEVEL_INTERVAL_MS = 3000L;
    private static final Map<UUID, Integer> levels = new HashMap<>();
    private static final Map<UUID, Long> lastLevelUp = new HashMap<>();

    @Override
    public void useAbility(@Nullable Player player, @Nullable Event event) {
        if (event != null) return;

        assert player != null;

        UUID uuid = player.getUniqueId();

        if (!player.isSprinting()) {
            levels.remove(uuid);
            lastLevelUp.remove(uuid);
            removePotionEffect(PotionEffectType.SPEED, player);
            return;
        }

        int currentLevel = levels.getOrDefault(uuid, 0);
        long now = System.currentTimeMillis();
        long last = lastLevelUp.getOrDefault(uuid, now);

        if (now - last >= LEVEL_INTERVAL_MS && now - last < LEVEL_INTERVAL_MS * 2) {
            currentLevel = Math.min(currentLevel + 1, 3);
            levels.put(uuid, currentLevel);
            lastLevelUp.put(uuid, now);
        }

        removePotionEffect(PotionEffectType.SPEED, player);
        if (currentLevel > 0) {
            addPotionEffect(PotionEffectType.SPEED, player, 2, currentLevel);
        }
    }
}
