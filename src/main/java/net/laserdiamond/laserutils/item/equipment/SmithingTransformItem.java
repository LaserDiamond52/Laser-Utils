package net.laserdiamond.laserutils.item.equipment;

import net.minecraft.world.level.ItemLike;

/**
 * Allows the item to be crafted in a Smithing Table
 */
public interface SmithingTransformItem {

    /**
     * @return The equipment piece to use to craft the item
     */
    ItemLike equipmentPiece();

    /**
     * @return The template item to use to craft the item
     */
    ItemLike templateItem();

    /**
     * @return The material item to use to craft the item
     */
    ItemLike materialItem();
}
