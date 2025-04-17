package org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoader;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoaderManager;

public class BlockPlaceHandler implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        Block block = e.getBlockPlaced();
        if (item.getType() != block.getType() || !NBTManager.hasTag(item, "chunk_loader")) return;

        String chunkLoaderType = NBTManager.getString(item, "chunk_loader");
        ChunkLoaderManager.register(new ChunkLoader(block.getLocation(), chunkLoaderType));
    }
}
