package org.satellite.dev.progiple.mcchunkloader.chunkloaders;

import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.mcchunkloader.McChunkLoader;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.particles.SpawnParticleTask;
import org.satellite.dev.progiple.mcchunkloader.configs.Config;
import org.satellite.dev.progiple.mcchunkloader.configs.LocationConfig;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class ChunkLoader {
    private final Location location;
    private final String id;
    private final Set<Chunk> loadableChunks = new HashSet<>();
    private final SpawnParticleTask particleTask;
    public ChunkLoader(Location location, String id) {
        String path = String.format("chunk_loaders.%s", id);
        ConfigurationSection chunkLoaderSection = Config.getSection(path);

        this.location = location;
        this.id = id;

        Block block = this.location.getBlock();
        if (block.getType() == Material.AIR) {
            Material material = Material.getMaterial(Objects.requireNonNull(chunkLoaderSection.getString("material")));
            block.setType(material == null ? Material.STONE : material);
        }

        this.registerChunks(chunkLoaderSection.getInt("chunk_radius"));
        this.particleTask = new SpawnParticleTask(chunkLoaderSection.getConfigurationSection("particles"), this.location);
    }

    public void registerChunks(int chunk_radius) {
        this.loadableChunks.clear();

        Chunk centerChunk = this.location.getChunk();
        World world = centerChunk.getWorld();

        int centerChunkX = centerChunk.getX();
        int centerChunkZ = centerChunk.getZ();

        for (int dx = -chunk_radius; dx <= chunk_radius; dx++) {
            for (int dz = -chunk_radius; dz <= chunk_radius; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;

                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                this.loadableChunks.add(chunk);
            }
        }
    }

    public void remove() {
        LocationConfig.removeLocation(this.location);
        this.particleTask.cancel();
    }

    public void startParticleTask() {
        this.particleTask.runTaskAsynchronously(McChunkLoader.getINSTANCE());
    }
}
