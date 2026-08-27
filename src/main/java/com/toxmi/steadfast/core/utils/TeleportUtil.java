package com.toxmi.steadfast.core.utils;

import com.toxmi.steadfast.modules.claims.ClaimManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;

public class TeleportUtil {
    private static final ClaimManager claimManager = ClaimManager.get();

    public static void tryRandomTeleport(org.bukkit.entity.Player bukkitEntity, double radius) {
        LivingEntity user = ((CraftLivingEntity) bukkitEntity).getHandle();
        ServerLevel world = (ServerLevel) bukkitEntity.getWorld();


        // Taken from net.minecraft.world.item.ChorusFruitItem;
        if (user.isPassenger()) {
            user.stopRiding();
        }
        for (int i = 0; i < 16; ++i) {
            double x = user.getX() + (user.getRandom().nextDouble() - (double) 0.5F) * radius;
            double y = Mth.clamp(user.getY() + (double) (user.getRandom().nextInt(16) - 8), world.getMinY(), (world.getMinY() + (world).getLogicalHeight() - 1));
            double z = user.getZ() + (user.getRandom().nextDouble() - (double) 0.5F) * radius;

            Vec3 vec3D = user.position();
            if (world.getWorldBorder().isWithinBounds(x, y, z)) continue;
            if (!claimManager.isAllowedToUse(bukkitEntity, new Location(bukkitEntity.getWorld(), x, y ,z))) continue;
            Optional<Boolean> status = user.randomTeleport(x, y, z, true, PlayerTeleportEvent.TeleportCause.CONSUMABLE_EFFECT);
            if (status.isEmpty()) {
                break;
            }

            if (status.get()) {
                world.gameEvent(GameEvent.TELEPORT, vec3D, GameEvent.Context.of(user));
                SoundEvent soundEffect = SoundEvents.CHORUS_FRUIT_TELEPORT;
                SoundSource soundCategory = SoundSource.PLAYERS;

                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEffect, soundCategory);
                user.resetFallDistance();
                break;
            }
        }


    }
}
