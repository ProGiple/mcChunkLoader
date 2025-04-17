package org.satellite.dev.progiple.mcchunkloader;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoader;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.ChunkLoaderManager;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners.BlockBreakHandler;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners.BlockPlaceHandler;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.listeners.UnloadChunkHandler;
import org.satellite.dev.progiple.mcchunkloader.configs.LocationConfig;

public final class McChunkLoader extends LunaPlugin {
    @Getter
    private static McChunkLoader INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.initialize();

        saveDefaultConfig();
        this.loadFile("locations.yml");
        this.registerListeners(new BlockBreakHandler(), new BlockPlaceHandler(), new UnloadChunkHandler());
        this.registerTabExecutor(new Command(), "mcchunkloader");

        ConfigurationSection section = LocationConfig.getSection("locations");
        section.getKeys(false).forEach(k -> {
            Location location = LocationConfig.getLocation(k);
            if (location != null && ChunkLoaderManager.getChunkLoader(location) == null) {
                ChunkLoaderManager.register(new ChunkLoader(location, section.getString(String.format("%s.id", k))));
            }
        });
    }
}
