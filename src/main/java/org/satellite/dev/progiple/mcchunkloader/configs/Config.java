package org.satellite.dev.progiple.mcchunkloader.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.Configuration.IConfig;
import org.novasparkle.lunaspring.API.Menus.Items.NonMenuItem;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.satellite.dev.progiple.mcchunkloader.McChunkLoader;

@UtilityClass
public class Config {
    private final IConfig config;
    static {
        config = new IConfig(McChunkLoader.getINSTANCE());
    }

    public void reload() {
        config.reload(McChunkLoader.getINSTANCE());
    }

    public void sendMessage(CommandSender sender, String id, String... rpl) {
        config.sendMessage(sender, id, rpl);
    }

    public @NotNull ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public ItemStack getItem(String id) {
        ConfigurationSection section = getSection(String.format("chunk_loaders.%s", id));

        ItemStack item = new NonMenuItem(section).getItemStack();
        if (item == null) return null;

        if (item.getAmount() <= 0) item.setAmount(1);

        NBTManager.setString(item, "chunk_loader", id);
        return item;
    }
}
