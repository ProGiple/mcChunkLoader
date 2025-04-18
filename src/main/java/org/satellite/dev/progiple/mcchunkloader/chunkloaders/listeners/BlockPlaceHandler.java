package org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.AllowedType;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoader;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoaderManager;

import java.util.Arrays;

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

    @EventHandler
    public void onPlaceAllowedBlock(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        if (Arrays.stream(AllowedType.values()).noneMatch(t -> t.getMaterial() == block.getType())) return;

        boolean[] updated = {false};
        ChunkLoaderManager.getChunkLoaders().forEach(cl -> {
            if (cl.contains(block)) {
                cl.getLoadableBlocks().add(block);

                if (block.getType() == Material.SPAWNER && !updated[0]) {
                    CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
                    creatureSpawner.setRequiredPlayerRange(Integer.MAX_VALUE);
                    creatureSpawner.update();

                    updated[0] = true;
                }
            }
        });
    }
}
