package org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.AllowedType;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoader;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoaderManager;
import org.satellite.dev.progiple.mcchunkloader.configs.Config;

import java.util.Arrays;

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

    @EventHandler
    public void onBreakAllowedBlock(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (Arrays.stream(AllowedType.values()).noneMatch(t -> t.getMaterial() == block.getType())) return;
        ChunkLoaderManager.getChunkLoaders().forEach(cl -> cl.getLoadableBlocks().remove(block));
    }
}
