package org.satellite.dev.progiple.mcchunkloader.chunkloaders;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum AllowedType {
    WHEAT_SEEDS(PlacementType.PLANT),
    BAMBOO(PlacementType.BLOCK),
    SUGAR_CANE(PlacementType.BLOCK),
    CARROTS(PlacementType.PLANT),
    POTATOES(PlacementType.PLANT),
    BEETROOTS(PlacementType.PLANT),
    MELON_SEEDS(PlacementType.PLANT),
    PUMPKIN_SEEDS(PlacementType.PLANT),
    SPAWNER(PlacementType.NONE),
    CACTUS(PlacementType.BLOCK),
    KELP(PlacementType.BLOCK);

    private final Material material;
    private final PlacementType placementType;
    AllowedType(PlacementType placementType) {
        this.placementType = placementType;
        this.material = Material.getMaterial(this.name());
    }

    public enum PlacementType {
        BLOCK,
        PLANT,
        NONE
    }
}
