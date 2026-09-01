package com.toxmi.steadfast.core.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potion {

    /**
     * Adds a potion effect to an entity for the selected amount of time.
     *
     * @param type      Type of potion effect
     * @param entity    Entity who the potion effect is applied to
     * @param duration  How long the potion effect will last in seconds
     * @param amplifier What level of potion effect is added
     */
    public static void addPotionEffect(PotionEffectType type, LivingEntity entity, int duration, int amplifier) {
        amplifier -= 1;
        duration *= 20;
        entity.addPotionEffect(new PotionEffect(type, duration, amplifier));
    }

    /**
     * Adds a potion effect to an entity for the selected amount of time.
     *
     * @param type      Type of potion effect
     * @param entity    Entity who the potion effect is applied to
     * @param duration  How long the potion effect will last in seconds
     * @param amplifier What level of potion effect is added
     */
    public static void addPotionEffect(PotionEffectType type, LivingEntity entity, double duration, int amplifier) {
        amplifier -= 1;
        duration *= 20;
        entity.addPotionEffect(new PotionEffect(type, (int) duration, amplifier));
    }

    /**
     * Adds an infinite potion effect to an entity
     *
     * @param type      Type of potion effect
     * @param entity    Entity who the potion effect is applied to
     * @param amplifier What level of potion effect is added
     */
    public static void addPotionEffect(PotionEffectType type, LivingEntity entity, int amplifier) {
        amplifier -= 1;
        entity.addPotionEffect(new PotionEffect(type, -1, amplifier));
    }

    /**
     * Removes a potion effect from an entity
     *
     * @param type   Type of potion effect to be removed
     * @param entity Entity from which the potion effect is removed from
     */
    public static void removePotionEffect(PotionEffectType type, LivingEntity entity) {
        entity.removePotionEffect(type);

    }

    /**
     * Clears all potion effects from an entity
     *
     * @param entity Entity which potion effects are cleared
     */
    public static void clearPotionEffects(LivingEntity entity) {
        entity.clearActivePotionEffects();

    }

}
