package com.toxmi.steadfast.core.utils;

import com.toxmi.steadfast.Steadfast;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class Scheduler{
    protected final Steadfast plugin;
    private static Scheduler instance;

    public Scheduler(Steadfast plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static synchronized Scheduler get() {
        if (instance == null) {
            instance = new Scheduler(Steadfast.get());
        }
        return instance;
    }

    /**
     * Schedules a runnable to run once in the region at the given location.
     *
     * @param loc the location of the target region
     * @param runnable the code to execute
     * @return the scheduled task handle
     */
    public ScheduledTask region(Location loc, Runnable runnable) {
        return plugin.getServer().getRegionScheduler().run(plugin,loc, task -> runnable.run());
    }

    /**
     * Schedules a runnable to run once in the region at the given location after an initial delay.
     *
     * @param loc the location of the target region
     * @param runnable the code to execute
     * @param initialDelay the delay in ticks before the task starts
     * @return the scheduled task handle
     */
    public ScheduledTask region(Location loc, Runnable runnable,int initialDelay) {
        return plugin.getServer().getRegionScheduler().runDelayed(plugin,loc, task -> runnable.run(), initialDelay);
    }

    /**
     * Schedules a runnable to run repeatedly in the region at the given location.
     *
     * @param loc the location of the target region
     * @param runnable the code to execute
     * @param initialDelay the delay in ticks before the first execution
     * @param delay the delay between executions in ticks
     * @return the scheduled task handle
     */
    public ScheduledTask region(Location loc, Runnable runnable, int initialDelay, long delay) {
        return plugin.getServer().getRegionScheduler().runAtFixedRate(plugin,loc, task -> runnable.run(), initialDelay, delay);
    }

    /**
     * Schedules a runnable to run repeatedly using the global region scheduler.
     *
     * @param runnable the code to execute
     * @param initialDelay the delay in ticks before the first execution
     * @param delay the delay between executions in ticks
     * @return the scheduled task handle
     */
    public ScheduledTask globalRegion(Runnable runnable, long initialDelay, long delay) {
        return plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(), initialDelay, delay);
    }

    /**
     * Schedules a runnable to run once using the global region scheduler after an initial delay.
     *
     * @param runnable the code to execute
     * @param initialDelay the delay in ticks before the task starts
     * @return the scheduled task handle
     */
    public ScheduledTask globalRegion(Runnable runnable,long initialDelay) {
        return plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), initialDelay);
    }

    /**
     * Schedules a runnable to run once for the given player.
     *
     * @param player the player for whom the task is scheduled
     * @param runnable the code to execute
     * @return the scheduled task handle
     */
    public ScheduledTask playerScheduler(Player player, Runnable runnable) {
        return player.getScheduler().run(plugin, task -> runnable.run(), () -> {});
    }

    /**
     * Schedules a runnable to run repeatedly for the given player.
     *
     * @param player the player for whom the task is scheduled
     * @param runnable the code to execute
     * @param initialDelay the delay in ticks before the first execution
     * @param delay the delay between executions in ticks
     * @return the scheduled task handle
     */
    public ScheduledTask playerScheduler(Player player, Runnable runnable, int initialDelay, long delay) {
        return player.getScheduler().runAtFixedRate(plugin, task -> runnable.run(), () -> {}, initialDelay, delay);
    }

    /**
     * Schedules a runnable to run once for the given player after a delay.
     *
     * @param player the player for whom the task is scheduled
     * @param runnable the code to execute
     * @param delay the delay before the task starts in ticks
     * @return the scheduled task handle
     */
    public ScheduledTask playerScheduler(Player player, Runnable runnable, long delay) {
        return player.getScheduler().runDelayed(plugin, task -> runnable.run(), () -> {}, delay);
    }

    /**
     * Schedules a runnable to run once for the given entity.
     *
     * @param entity the entity for whom the task is scheduled
     * @param runnable the code to execute
     * @return the scheduled task handle
     */
    public ScheduledTask entity (Entity entity, Runnable runnable) {
        return entity.getScheduler().run(plugin, task -> runnable.run(), () -> {});
    }

    /**
     * Schedules a runnable to run repeatedly for the given entity.
     *
     * @param entity the entity for whom the task is scheduled
     * @param runnable the code to execute
     * @param initialDelay the delay in ticks before the first execution
     * @param delay the delay between executions in ticks
     * @return the scheduled task handle
     */
    public ScheduledTask entity (Entity entity, Runnable runnable, long initialDelay, int delay) {
        return entity.getScheduler().runAtFixedRate(plugin, task -> runnable.run(), () -> {}, initialDelay, delay);
    }

    /**
     * Schedules a runnable to run immediately on the async scheduler.
     *
     * @param runnable the code to execute
     * @return the scheduled task handle
     */
    public ScheduledTask async (Runnable runnable) {
        return plugin.getServer().getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    /**
     * Schedules a runnable to run once on the async scheduler after a delay.
     *
     * @param runnable the code to execute
     * @param delay the delay before the task starts in seconds
     * @return the scheduled task handle
     */
    public ScheduledTask async (Runnable runnable,long delay) {
        return plugin.getServer().getAsyncScheduler().runDelayed(plugin, task -> runnable.run(),delay, TimeUnit.SECONDS);
    }

    /**
     * Schedules a runnable to run repeatedly on the async scheduler.
     *
     * @param runnable the code to execute
     * @param initialDelay the delay in seconds before the first execution
     * @param delay the delay between executions in seconds
     * @return the scheduled task handle
     */
    public ScheduledTask async (Runnable runnable,long initialDelay, long delay) {
        return plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), initialDelay, delay, TimeUnit.SECONDS);
    }
}
