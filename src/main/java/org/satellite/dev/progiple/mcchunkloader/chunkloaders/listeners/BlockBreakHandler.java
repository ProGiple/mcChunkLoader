package org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoader;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoaderManager;
import org.satellite.dev.progiple.mcchunkloader.configs.Config;

public class BlockBreakHandler implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Location location = e.getBlock().getLocation();

        ChunkLoader chunkLoader = ChunkLoaderManager.getChunkLoader(location);
        if (chunkLoader == null) return;

        ChunkLoaderManager.unregister(chunkLoader);
        ItemStack item = Config.getItem(chunkLoader.getId());
        if (item != null) location.getWorld().dropItem(location, item);
    }
}
