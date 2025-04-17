package org.satellite.dev.progiple.mcchunkloader;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.Util.utilities.Utils;
import org.satellite.dev.progiple.mcchunkloader.configs.Config;
import org.satellite.dev.progiple.mcchunkloader.configs.LocationConfig;

import java.util.List;
import java.util.stream.Collectors;

public class Command implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("mcchunkloader.admin")) {
            Config.sendMessage(commandSender, "noPermission");
            return true;
        }

        if (strings.length >= 1) {
            if (strings[0].equalsIgnoreCase("reload")) {
                Config.reload();
                LocationConfig.reload();
                Config.sendMessage(commandSender, "reload");
                return true;
            }

            if (strings[0].equalsIgnoreCase("give")) {
                Player player = strings.length >= 2 ? Bukkit.getPlayerExact(strings[1]) :
                        (commandSender instanceof Player ? (Player) commandSender : null);
                if (player == null || !player.isOnline()) {
                    Config.sendMessage(commandSender, "playerIsUnknown");
                    return true;
                }

                String id = strings.length >= 3 ? strings[2] : null;
                if (id == null || id.isEmpty()) {
                    Config.sendMessage(commandSender, "unknownItemId");
                    return true;
                }

                ItemStack item = Config.getItem(id);
                if (item != null) {
                    player.getInventory().addItem(item);
                    Config.sendMessage(commandSender, "giving", id, player.getName());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return List.of("reload", "give");
        }
        else if (strings.length == 2 && strings[0].equalsIgnoreCase("give")) {
            return Utils.getPlayerNicks(strings[1]);
        }
        else if (strings.length == 3 && strings[0].equalsIgnoreCase("give")) {
            return Config.getSection("chunk_loaders").getKeys(false)
                    .stream()
                    .filter(k -> k.toUpperCase().startsWith(strings[2].toUpperCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
