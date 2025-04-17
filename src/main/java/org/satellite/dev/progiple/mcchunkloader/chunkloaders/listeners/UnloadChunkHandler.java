package org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoaderManager;

public class UnloadChunkHandler implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent e) {
        final Chunk currentChunk = e.getChunk();
        World world = currentChunk.getWorld();

        int x = currentChunk.getX();
        int z = currentChunk.getZ();

        System.out.println("cX: " + x);
        System.out.println("cZ: " + z);
        if (ChunkLoaderManager.getChunkLoaders().stream().anyMatch(cl ->
                cl.getLoadableChunks().stream().anyMatch(c -> {
                    System.out.println("x " + c.getX());
                    System.out.println("z " + c.getZ());
                    return c.getX() == x && c.getZ() == z;
                }))) {
            System.out.println(currentChunk);

            world.loadChunk(x, z);
            world.loadChunk(x, z, true);
        }
    }
}
