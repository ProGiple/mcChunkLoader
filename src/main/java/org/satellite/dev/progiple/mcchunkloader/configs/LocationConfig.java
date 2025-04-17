package org.satellite.dev.progiple.mcchunkloader.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.Configuration.Configuration;
import org.novasparkle.lunaspring.API.Util.utilities.Utils;
import org.satellite.dev.progiple.mcchunkloader.McChunkLoader;

import java.io.File;

@UtilityClass
public class LocationConfig {
    private final Configuration config;
    static {
        config = new Configuration(new File(McChunkLoader.getINSTANCE().getDataFolder(), "locations.yml"));
    }

    public void reload() {
        config.reload();
    }

    public ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public void setLocation(Location location, String id) {
        String path = String.format("locations.%s", Utils.getRKey((byte) 32));
        config.setLocation(path + ".location", location, true, true);
        config.setString(path + ".id", id);

        config.save();
    }

    public void removeLocation(Location location) {
        ConfigurationSection section = getSection("locations");
        String id = section.getKeys(false).stream().filter(k -> getLocation(k).equals(location)).findFirst().orElse(null);
        if (id != null && !id.isEmpty()) {
            config.set(String.format("locations.%s", id), null);
            config.save();
        }
    }

    public Location getLocation(@NotNull String id) {
        return config.getLocation(String.format("locations.%s.location", id));
    }
}
