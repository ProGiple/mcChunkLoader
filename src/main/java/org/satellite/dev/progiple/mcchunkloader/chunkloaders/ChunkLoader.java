package org.satellite.dev.progiple.mcchunkloader.chunkloaders;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.Util.utilities.LunaTask;
import org.satellite.dev.progiple.mcchunkloader.McChunkLoader;
import org.satellite.dev.progiple.mcchunkloader.chunkloaders.particles.SpawnParticleTask;
import org.satellite.dev.progiple.mcchunkloader.configs.Config;
import org.satellite.dev.progiple.mcchunkloader.configs.LocationConfig;

import java.util.*;

@Getter
public class ChunkLoader {
    private final Location location;
    private final String id;
    private final Set<Block> loadableBlocks = new HashSet<>();
    private final SpawnParticleTask particleTask;
    private final Task task = new Task(this);

    private int radius;
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

        Bukkit.getScheduler().runTaskAsynchronously(McChunkLoader.getINSTANCE(), () -> this.registerChunks(chunkLoaderSection.getInt("radius")));
        this.particleTask = new SpawnParticleTask(chunkLoaderSection.getConfigurationSection("particles"), this.location);
    }

    public void registerChunks(int radius) {
        this.loadableBlocks.clear();

        World world = this.location.getWorld();
        if (radius <= 0) return;
        this.radius = radius;

        int min = world.getMinHeight();
        int max = world.getMaxHeight();
        for (int x = this.location.getBlockX() - radius; x < this.location.getBlockX() + radius; x++) {
            for (int z = this.location.getBlockZ() - radius; z < this.location.getBlockZ() + radius; z++) {
                for (int y = min; y < max; y++) {
                    Block block = world.getBlockAt(x, y, z);

                    Material material = block.getType();
                    if (Arrays.stream(AllowedType.values()).anyMatch(t -> t.getMaterial() == material)) {
                        this.loadableBlocks.add(block);
                        Bukkit.getScheduler().runTask(McChunkLoader.getINSTANCE(), () -> {
                            if (material == Material.SPAWNER && block.getState() instanceof CreatureSpawner) {
                                CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
                                creatureSpawner.setRequiredPlayerRange(Integer.MAX_VALUE);
                                creatureSpawner.update();
                            }
                        });
                    }
                }
            }
        }

        this.task.cancel();
        this.task.runTaskAsynchronously(McChunkLoader.getINSTANCE());
    }

    public boolean contains(Block block) {
        int x = block.getX();
        int z = block.getZ();

        int minX = this.location.getBlockX() - this.radius;
        int maxX = this.location.getBlockX() + this.radius;
        int minZ = this.location.getBlockZ() - this.radius;
        int maxZ = this.location.getBlockZ() + this.radius;

        return minX <= x && x <= maxX && minZ <= z && z <= maxZ;
    }

    public void remove() {
        LocationConfig.removeLocation(this.location);
        this.task.cancel();
        this.particleTask.cancel();
    }

    public void startParticleTask() {
        this.particleTask.runTaskAsynchronously(McChunkLoader.getINSTANCE());
    }

    public static class Task extends LunaTask {
        private final ChunkLoader chunkLoader;
        public Task(ChunkLoader chunkLoader) {
            super(0);
            this.chunkLoader = chunkLoader;
        }

        @Override @SneakyThrows @SuppressWarnings("all")
        public void start() {
            ConfigurationSection optionSections = Config.getSection("loaders");
            int spawner_tick = optionSections.getInt("default_spawner_load_seconds");
            int plants_tick = optionSections.getInt("default_plants_load_seconds");
            int spawn_amount = optionSections.getInt("max_spawn_entities");

            int timer = 0;
            World world = this.chunkLoader.location.getWorld();
            while (true) {
                if (!this.isActive()) return;

                if (plants_tick > 0 && timer % plants_tick == 0) {
                    Bukkit.getScheduler().runTask(McChunkLoader.getINSTANCE(), () -> {
                        new HashSet<>(this.chunkLoader.getLoadableBlocks()).forEach(b -> {
                            AllowedType.PlacementType placementType = AllowedType.valueOf(b.getType().name()).getPlacementType();
                            if (placementType == AllowedType.PlacementType.PLANT) {
                                Bukkit.getScheduler().runTask(McChunkLoader.getINSTANCE(), () -> {
                                    Ageable ageable = (Ageable) b.getBlockData();

                                    int currentAge = ageable.getAge();
                                    if (currentAge < ageable.getMaximumAge()) {
                                        ageable.setAge(currentAge + 1);
                                        b.setBlockData(ageable);
                                    }
                                });
                            } else if (placementType == AllowedType.PlacementType.BLOCK) {
                                if (b.getLocation().clone().add(0, -2, 0).getBlock().getType() != b.getType()) {
                                    Location above = b.getLocation().clone().add(0, 1, 0);

                                    Block aboveBlock = above.getBlock();
                                    if (aboveBlock.getType() == Material.AIR) {
                                        aboveBlock.setType(b.getType());
                                        this.chunkLoader.getLoadableBlocks().add(aboveBlock);
                                    }
                                }
                            }
                        });
                    });
                }
                timer++;
                Thread.sleep(1000L);
            }
        }
    }
}
