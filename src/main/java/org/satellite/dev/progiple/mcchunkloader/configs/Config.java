package org.satellite.dev.progiple.mcchunkloader.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.Configuration.IConfig;
import org.novasparkle.lunaspring.API.Menus.Items.NonMenuItem;
import org.novasparkle.lunaspring.API.Util.Service.managers.ColorManager;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.satellite.dev.progiple.mcchunkloader.McChunkLoader;

import java.util.List;
import java.util.stream.Collectors;

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

    @SuppressWarnings("deprecation")
    public ItemStack getItem(String id) {
        ConfigurationSection section = getSection(String.format("chunk_loaders.%s", id));
        NonMenuItem nonMenuItem = new NonMenuItem(section) {
            public ItemStack getDefaultStack() {
                ItemStack item = new ItemStack(getMaterial(), 1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ColorManager.color(getDisplayName()));

                List<String> lore = getLore();
                lore.replaceAll(ColorManager::color);
                meta.setLore(lore);

                item.setItemMeta(meta);
                NBTManager.setString(item, "chunk_loader", id);

                return item;
            }
        };

        return nonMenuItem.getDefaultStack();
    }
}
