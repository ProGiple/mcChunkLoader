package org.satellite.dev.progiple.mcchunkloader.chunkloaders;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.satellite.dev.progiple.mcchunkloader.configs.LocationConfig;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ChunkLoaderManager {
    @Getter
    private final Set<ChunkLoader> chunkLoaders = new HashSet<>();

    public ChunkLoader getChunkLoader(Location location) {
        return chunkLoaders.stream().filter(c -> c.getLocation().equals(location)).findFirst().orElse(null);
    }

    public void register(ChunkLoader chunkLoader) {
        chunkLoaders.add(chunkLoader);
        LocationConfig.setLocation(chunkLoader.getLocation(), chunkLoader.getId());
        chunkLoader.startParticleTask();
    }

    public void unregister(ChunkLoader chunkLoader) {
        chunkLoaders.remove(chunkLoader);
        chunkLoader.remove();
    }
}
