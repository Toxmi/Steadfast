package com.toxmi.steadfast.modules.claims;

import org.bukkit.Chunk;
import org.bukkit.Location;

public record ChunkPos(String world, int x, int z) {

    public static ChunkPos of(Chunk chunk) {
        return new ChunkPos(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static ChunkPos of(Location location) {
        return new ChunkPos(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public static ChunkPos fromKey(String chunkKey) {
        String[] parts = chunkKey.split(":");
        return new ChunkPos(parts[2], Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public String toKey() {
        return x + ":" + z + ":" + world;
    }
}
